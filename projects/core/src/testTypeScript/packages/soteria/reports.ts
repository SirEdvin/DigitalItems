import { withTermColor } from "cc-corelib/utils";
import { TestReport, TestResult } from "./base";

export function printReports(reports: TestReport[]): void {
    for (const report of reports) {
        withTermColor(report.result == TestResult.SUCCESS ? colors.green : colors.red, () => {
            print("Test", report.name, report.result, report.message);
        });
    }
}
