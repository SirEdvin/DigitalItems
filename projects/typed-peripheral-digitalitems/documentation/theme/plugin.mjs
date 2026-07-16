import { JSX } from "typedoc";

export function load(app) {
  app.renderer.hooks.on("sidebar.begin", () =>
    JSX.createElement(
      "section",
      { class: "di-version-panel", "aria-label": "Documentation version" },
      JSX.createElement("span", { class: "di-version-label" }, "Version"),
      JSX.createElement(
        "select",
        { id: "di-version-select", disabled: true },
        JSX.createElement("option", null, "Local build")
      )
    )
  );
}
