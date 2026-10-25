#!/usr/bin/env python3
"""Reject empty, skipped, incomplete or failing DigitalItems GameTest runs."""
import json
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

EXPECTED = {
    "basedigitize", "remotedigitize", "basemoredigitize", "remotemoredigitize",
    "pocketrematerializeconservation", "turtlerematerializeconservation", "blockrematerializeconservation",
    "baselimitdigitize", "remotelimitdigitize", "fluiddigitize", "energydigitize", "olddigitize", "sameversionpersistence",
}


def check_report(path):
    root = ET.parse(path).getroot()
    cases = root.findall(".//testcase")
    assert cases, f"{path}: no executed cases"
    assert not root.findall(".//failure") and not root.findall(".//error"), f"{path}: failures/errors"
    assert not root.findall(".//skipped"), f"{path}: unexpectedly skipped tests"
    suites = [root] if root.tag == "testsuite" else root.findall(".//testsuite")
    assert sum(int(s.get("tests", "0")) for s in suites) == len(cases), f"{path}: count mismatch"
    names = [c.get("name", "").split(".")[-1].lower().replace("_", "") for c in cases]
    assert len(names) == len(EXPECTED) and set(names) == EXPECTED, f"{path}: unexpected inventory {names}"
    return {"report": str(path), "tests": len(cases), "names": names}


if __name__ == "__main__":
    assert len(sys.argv) > 1, "Pass the exact report paths for this run"
    print(json.dumps([check_report(Path(path)) for path in sys.argv[1:]], indent=2))
