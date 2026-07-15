(() => {
  const selector = document.querySelector("#di-version-select");
  if (!(selector instanceof HTMLSelectElement)) return;

  const script = document.currentScript ?? [...document.scripts].find((entry) =>
    entry.src.endsWith("/custom.js")
  );
  if (!script) return;

  const scriptUrl = new URL(script.src);
  const versionRoot = scriptUrl.pathname.match(
    /^(.*\/)(?:branch|tag)\/[^/]+\/assets\/custom\.js$/
  );
  if (!versionRoot) return;

  const siteRoot = new URL(versionRoot[1], scriptUrl.origin);
  const currentPath = decodeURIComponent(
    scriptUrl.pathname.slice(versionRoot[1].length).replace(/assets\/custom\.js$/, "")
  );

  fetch(new URL("versions.json", siteRoot))
    .then((response) => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return response.json();
    })
    .then((manifest) => {
      if (!manifest || !Array.isArray(manifest.versions)) {
        throw new Error("Unsupported version manifest");
      }

      selector.replaceChildren();
      const groups = new Map([
        ["branch", document.createElement("optgroup")],
        ["tag", document.createElement("optgroup")],
      ]);
      groups.get("branch").label = "Development branches";
      groups.get("tag").label = "Releases";

      for (const entry of manifest.versions) {
        if (!entry || !groups.has(entry.kind) || typeof entry.path !== "string") continue;
        const option = document.createElement("option");
        option.value = entry.path;
        option.textContent = entry.label || entry.name;
        option.selected = entry.path === currentPath;
        groups.get(entry.kind).append(option);
      }

      for (const group of groups.values()) {
        if (group.children.length > 0) selector.append(group);
      }
      selector.disabled = selector.options.length === 0;
    })
    .catch(() => {
      selector.options[0].textContent = "Version list unavailable";
    });

  selector.addEventListener("change", () => {
    if (!selector.value) return;
    window.location.assign(new URL(selector.value, siteRoot));
  });
})();
