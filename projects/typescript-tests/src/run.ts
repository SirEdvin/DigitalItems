import { printReports, Test, TestResult, TestSuite } from "@siredvin/soteria";

export function runTest(suiteName: string, test: Test): void {
    const suite = new TestSuite(suiteName);
    suite.addTest(test);
    const reports = suite.run();
    printReports(reports);
    const report = reports[0];
    if (report.result != TestResult.SUCCESS) throw report.result + ": " + report.message;
}
