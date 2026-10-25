#!/usr/bin/env python3
"""Audit the exact versioned release outputs, not stale recursive build trees."""

import hashlib
import json
from pathlib import Path
import struct
import zipfile


def classes(build, source_set):
    result = set()
    for language in ("java", "kotlin"):
        root = build / "classes" / language / source_set
        result.update(p.relative_to(root).as_posix() for p in root.rglob("*.class"))
    return result


def audit():
    properties = {key.strip(): value.strip() for line in Path("gradle.properties").read_text().splitlines()
                  if "=" in line and not line.startswith("#") for key, _, value in [line.partition("=")]}
    version = properties["modVersion"]
    result = []
    for minecraft, library, major in [("1.20.1", "2.0.0-rc.1", 61), ("1.21.1", "2.0.0-rc.1", 65), ("26.1.2", "2.0.0-rc.1", 69)]:
        common = Path("projects/core/versions") / minecraft / "build"
        for loader in ("fabric", "forge"):
            build = Path("projects") / loader / "versions" / minecraft / "build"
            binary = build / f"libs/digitalitems-{loader}-{minecraft}-{version}.jar"
            with zipfile.ZipFile(binary) as archive:
                entries = archive.namelist()
                assert len(entries) == len(set(entries)), f"{binary}: duplicate ZIP entries"
                actual = {name for name in entries if name.endswith(".class")}
                expected = classes(common, "main") | classes(build, "main")
                assert expected and actual == expected, f"{binary}: class inventory mismatch {actual ^ expected}"
                fixtures = classes(common, "testMod") | classes(build, "testMod") | classes(build, "docsRender")
                assert fixtures, f"{binary}: compile fixture classes before auditing"
                assert not actual.intersection(fixtures), f"{binary}: test/docs classes leaked"
                assert not any("testiarium" in name.lower() or "/.cache/" in name for name in entries), f"{binary}: test dependency/cache leaked"
                assert all(struct.unpack(">H", archive.read(name)[6:8])[0] <= major for name in actual), f"{binary}: wrong JVM target"
                if minecraft != "26.1.2":
                    assert all(b"net/minecraft/resources/Identifier" not in archive.read(name) and
                               b"net/minecraft/world/level/saveddata/SavedDataType" not in archive.read(name)
                               for name in actual), f"{binary}: target API leaked into a legacy node"
                for old, new, count in [("advancements", "advancement", 14), ("recipes", "recipe", 2), ("loot_tables", "loot_table", 2),
                                         ("computercraft/turtle_upgrades", "computercraft/turtle_upgrade", 2), ("computercraft/pocket_upgrades", "computercraft/pocket_upgrade", 2)]:
                    right, wrong = (old, new) if minecraft == "1.20.1" else (new, old)
                    prefix = f"data/digitalitems/{right}/"
                    assert sum(name.startswith(prefix) and name.endswith(".json") for name in entries) == count, f"{binary}: wrong {right} inventory"
                    assert not any(name.startswith(f"data/digitalitems/{wrong}/") for name in entries), f"{binary}: wrong-version resources"
                if loader == "fabric":
                    metadata = json.loads(archive.read("fabric.mod.json"))
                    assert metadata["id"] == "digitalitems" and metadata["version"] == version
                    assert metadata["depends"]["java"] == f">={major - 44}"
                    if minecraft == "26.1.2":
                        assert metadata["depends"]["minecraft"] == minecraft
                        assert archive.read(metadata["accessWidener"]).startswith(b"accessWidener v2 official")
                    nested = [entry["file"] for entry in metadata["jars"]]
                    assert sorted(nested) == [f"META-INF/jars/{name}-fabric-{minecraft}-{library}.jar" for name in ("broccolium", "tweakium")]
                    assert not any(name.endswith("mods.toml") for name in entries)
                else:
                    metadata_path = "META-INF/" + ("mods.toml" if minecraft == "1.20.1" else "neoforge.mods.toml")
                    metadata = archive.read(metadata_path).decode()
                    assert "${" not in metadata and f'version="{version}"' in metadata
                    nested_metadata = json.loads(archive.read("META-INF/jarjar/metadata.json"))["jars"]
                    assert len(nested_metadata) == 2 and all(entry["version"]["artifactVersion"] == library for entry in nested_metadata)
                    assert all(entry["version"]["range"] == f"[{library}]" for entry in nested_metadata), f"{binary}: jar-in-jar must require the qualified RC"
                    nested = [entry["path"] for entry in nested_metadata]
                    assert "fabric.mod.json" not in entries
                    assert ("digitalitems.mixins.json" in entries) == (minecraft == "1.20.1")
                assert all(name in entries for name in nested), f"{binary}: missing nested dependencies"
                nested_hashes = {name: hashlib.sha256(archive.read(name)).hexdigest() for name in nested}
                if minecraft == "26.1.2":
                    pack = json.loads(archive.read("pack.mcmeta"))["pack"]
                    assert pack["min_format"] == [84, 0] and pack["max_format"] == [101, 1], f"{binary}: client/data pack formats"
                    for upgrade in ("digitizer", "advanced_digitizer"):
                        model = json.loads(archive.read(f"assets/digitalitems/computercraft/turtle_upgrade/{upgrade}.json"))
                        assert model["type"] == "computercraft:sided"
                        for side in ("left", "right"):
                            assert model[side] == f"digitalitems:turtle/{upgrade}_{side}"
                            assert f"assets/digitalitems/models/turtle/{upgrade}_{side}.json" in entries
            with zipfile.ZipFile(binary.with_name(binary.stem + "-sources.jar")) as source_archive:
                assert any(name.endswith("DigitalItemsSavedData.kt") for name in source_archive.namelist()), f"{binary}: common sources missing"
            for name in ("pom-default.xml", "module.json"):
                metadata = (build / "publications/maven" / name).read_text()
                assert "testiarium" not in metadata and "testMod" not in metadata, f"{binary}: published fixture dependency"
            result.append({"minecraft": minecraft, "loader": loader, "path": str(binary), "sha256": hashlib.sha256(binary.read_bytes()).hexdigest(),
                           "bytes": binary.stat().st_size, "classes": len(actual), "nested": nested, "nested_sha256": nested_hashes,
                           "fixture_classes_checked": len(fixtures)})
    return result


if __name__ == "__main__":
    print(json.dumps(audit(), indent=2))
