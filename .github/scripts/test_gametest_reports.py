import runpy
from pathlib import Path
from tempfile import TemporaryDirectory
import unittest
import xml.etree.ElementTree as ET

MODULE = runpy.run_path(str(Path(__file__).with_name("check-gametest-reports.py")))


class ReportTests(unittest.TestCase):
    def test_target_native_inventory(self):
        with TemporaryDirectory() as directory:
            path = Path(directory) / "26.1.2" / "report.xml"
            path.parent.mkdir()
            expected = MODULE["TARGET_EXPECTED"]
            root = ET.Element("testsuite", tests=str(len(expected)))
            for name in sorted(expected):
                ET.SubElement(root, "testcase", name=name)
            ET.ElementTree(root).write(path)
            self.assertEqual(MODULE["check_report"](path)["tests"], len(expected))
            root.remove(root[0])
            root.set("tests", str(len(root)))
            ET.ElementTree(root).write(path)
            with self.assertRaises(AssertionError):
                MODULE["check_report"](path)

    def test_inventory_and_failure_guards(self):
        with TemporaryDirectory() as directory:
            path = Path(directory) / "report.xml"
            def report():
                root = ET.Element("testsuite", tests=str(len(MODULE["EXPECTED"])))
                for name in sorted(MODULE["EXPECTED"]):
                    ET.SubElement(root, "testcase", name=f"DigitalItemsGameTests.{name}")
                return root
            def check(root):
                ET.ElementTree(root).write(path)
                return MODULE["check_report"](path)
            self.assertEqual(check(report())["tests"], len(MODULE["EXPECTED"]))
            for tag in ["failure", "error", "skipped"]:
                with self.subTest(tag=tag):
                    root = report()
                    ET.SubElement(root[0], tag)
                    with self.assertRaises(AssertionError):
                        check(root)
            for mutation in ["empty", "count", "duplicate"]:
                with self.subTest(mutation=mutation):
                    root = report()
                    if mutation == "empty":
                        root.clear()
                    elif mutation == "count":
                        root.set("tests", "999")
                    else:
                        root[0].set("name", root[1].get("name", ""))
                    with self.assertRaises(AssertionError):
                        check(root)


if __name__ == "__main__":
    unittest.main()
