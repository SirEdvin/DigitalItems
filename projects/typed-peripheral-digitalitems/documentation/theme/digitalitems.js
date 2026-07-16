document.addEventListener("DOMContentLoaded", () => {
  const script = [...document.scripts].find((entry) => entry.src.endsWith("/assets/javascripts/digitalitems.js"));
  if (!script) return;
  const scriptUrl = new URL(script.src);
  const match = scriptUrl.pathname.match(/^(.*\/)((?:branch|tag)\/[^/]+)\/assets\/javascripts\/digitalitems\.js$/);
  if (!match) return;

  const siteRoot = new URL(match[1], scriptUrl.origin);
  const currentVersion = `${decodeURIComponent(match[2])}/`;
  const select = document.createElement("select");
  select.id = "di-version-select";
  select.ariaLabel = "Documentation version";
  select.disabled = true;
  select.append(new Option("Loading versions..."));
  document.querySelector(".md-header__inner")?.append(select);

  fetch(new URL("versions.json", siteRoot))
    .then((response) => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return response.json();
    })
    .then((manifest) => {
      select.replaceChildren();
      for (const entry of manifest.versions ?? []) {
        const option = new Option(entry.label || entry.name, entry.path);
        option.selected = entry.path === currentVersion;
        select.append(option);
      }
      select.disabled = select.options.length === 0;
    })
    .catch(() => { select.options[0].textContent = "Versions unavailable"; });

  select.addEventListener("change", () => window.location.assign(new URL(select.value, siteRoot)));
});
