import { JSX, ReflectionKind } from "typedoc";

function renderPeripheralMethods(context) {
  if (!context.model.isDocument()) return;

  const interfaceName = context.model.frontmatter.peripheralInterface;
  if (typeof interfaceName !== "string") return;

  const peripheral = context.page.project
    .getReflectionsByKind(ReflectionKind.Interface)
    .find((reflection) => reflection.name === interfaceName);
  if (!peripheral?.children) return;

  const methods = peripheral.children.filter(
    (reflection) => reflection.kindOf(ReflectionKind.Method) && reflection.signatures?.length
  );
  if (methods.length === 0) return;

  context.page.pageHeadings.push({
    link: "#peripheral-methods",
    text: "Peripheral methods",
    level: 2,
  });

  return JSX.createElement(
    "section",
    { class: "di-method-reference", "aria-labelledby": "peripheral-methods" },
    JSX.createElement("h2", { id: "peripheral-methods" }, "Peripheral methods"),
    JSX.createElement(
      "p",
      { class: "di-generated-note" },
      "Generated from the TypeScript interface. Signatures, parameters, return values, and errors stay synchronized with the published API."
    ),
    ...methods.map((method) =>
      JSX.createElement(
        "article",
        { class: "di-method-card" },
        JSX.createElement("h3", null, method.name),
        ...method.signatures.map((signature) =>
          JSX.createElement(
            "div",
            { class: "di-method-overload" },
            context.memberSignatureTitle(signature),
            context.memberSignatureBody(signature, { hideSources: true })
          )
        )
      )
    )
  );
}

export function load(app) {
  app.renderer.hooks.on("body.begin", () =>
    JSX.createElement("div", {
      class: "di-signal-line",
      "aria-hidden": "true",
    })
  );

  app.renderer.hooks.on("sidebar.begin", () =>
    JSX.createElement(
      "section",
      { class: "di-version-panel", "aria-label": "Documentation version" },
      JSX.createElement("span", { class: "di-version-label" }, "DOCUMENTATION VERSION"),
      JSX.createElement(
        "select",
        { id: "di-version-select", disabled: true },
        JSX.createElement("option", null, "Local build")
      )
    )
  );

  app.renderer.hooks.on("content.end", renderPeripheralMethods);
}
