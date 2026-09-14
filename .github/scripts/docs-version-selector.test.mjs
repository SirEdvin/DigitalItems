import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import { runInNewContext } from "node:vm";
import test from "node:test";

const source = await readFile(new URL("../../projects/typed-peripheral-digitalitems/documentation/theme/digitalitems.js", import.meta.url), "utf8");

async function mount(manifest, { prefix = "/DigitalItems/", responseOk = true } = {}) {
  const events = {};
  const select = {
    options: [],
    append(option) { this.options.push(option); },
    replaceChildren(...options) { this.options = options; },
    addEventListener(name, callback) { events[name] = callback; },
  };
  const result = { select, events };
  runInNewContext(source, {
    URL,
    Option: function(text, value) { this.text = text; this.value = value; },
    document: {
      scripts: [{ src: `https://example.com${prefix}branch/1.20/assets/javascripts/digitalitems.js` }],
      addEventListener(name, callback) { assert.equal(name, "DOMContentLoaded"); callback(); },
      createElement(name) { assert.equal(name, "select"); return select; },
      querySelector(selector) {
        assert.equal(selector, ".md-header__title");
        return { after(element) { assert.equal(element, select); result.inserted = true; } };
      },
    },
    fetch: async (url) => {
      result.manifestUrl = url.href;
      return { ok: responseOk, status: 404, json: async () => manifest };
    },
    window: { location: { assign(url) { result.destination = url.href; } } },
  });
  await new Promise(setImmediate);
  return result;
}

const versions = [
  { kind: "branch", name: "1.21", minecraftVersion: "1.21.1", path: "branch/1.21/" },
  { kind: "branch", name: "1.20", minecraftVersion: "1.20.1", path: "branch/1.20/" },
  { kind: "tag", name: "v1.20.1-0.5.8", path: "tag/v1.20.1-0.5.8/" },
];

for (const prefix of ["/", "/DigitalItems/"]) {
  test(`header selector selects the current version and switches below ${prefix}`, async () => {
    const result = await mount({ versions }, { prefix });
    assert.equal(result.inserted, true);
    assert.equal(result.select.ariaLabel, "Documentation version");
    assert.equal(result.manifestUrl, `https://example.com${prefix}versions.json`);
    assert.equal(result.select.disabled, false);
    assert.deepEqual(result.select.options.map(({ text }) => text), ["Minecraft 1.21.1", "Minecraft 1.20.1", "v1.20.1-0.5.8"]);
    assert.equal(result.select.options[1].selected, true);
    result.select.value = versions[2].path;
    result.events.change();
    assert.equal(result.destination, `https://example.com${prefix}tag/v1.20.1-0.5.8/`);
  });
}

test("selector rejects destinations outside version directories", async () => {
  const result = await mount({ versions: [...versions, { path: "https://evil.example/" }, { path: "branch/../" }] });
  assert.equal(result.select.options.length, 3);
});

test("selector handles unavailable and malformed manifests", async () => {
  for (const [manifest, options] of [[{}, { responseOk: false }], [null, {}]]) {
    const { select } = await mount(manifest, options);
    assert.equal(select.disabled, true);
    assert.equal(select.options[0].text, "Versions unavailable");
  }
});
