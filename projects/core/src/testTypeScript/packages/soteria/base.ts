export interface Test {
    name: string;
    timeout: number;
    bootstrap(): void;
    execute(): void;
    teardown(): void;
}

export abstract class BasicTest implements Test {
    constructor(readonly name: string, readonly timeout: number = 10) {}
    abstract execute(): void;
    bootstrap(): void {}
    teardown(): void {}
}

export enum TestResult {
    SUCCESS = "SUCCESS",
    FAILED_ON_BOOTSTRAP = "FAILED_ON_BOOTSTRAP",
    FAILED = "FAILED",
    FAILED_ON_TEARDOWN = "FAILED_ON_TEARDOWN",
}

export class TestReport {
    constructor(readonly name: string, readonly result: TestResult, readonly message: string) {}
}

export class TestSuite {
    tests: Test[] = [];
    constructor(readonly name: string) {}
    addTest(test: Test): void { this.tests.push(test); }

    run(): TestReport[] {
        return this.tests.map((test) => {
            let report: TestReport | null = null;
            try {
                test.bootstrap();
            } catch (error) {
                return new TestReport(test.name, TestResult.FAILED_ON_BOOTSTRAP, tostring(error));
            }
            let executed = false;
            parallel.waitForAny(
                () => {
                    os.sleep(test.timeout);
                    if (!executed) report = new TestReport(test.name, TestResult.FAILED, "Failed with timeout");
                },
                () => {
                    try { test.execute(); } catch (error) {
                        report = new TestReport(test.name, TestResult.FAILED, tostring(error));
                    }
                    executed = true;
                },
            );
            try { test.teardown(); } catch (error) {
                if (report == null) return new TestReport(test.name, TestResult.FAILED_ON_TEARDOWN, tostring(error));
            }
            return report ?? new TestReport(test.name, TestResult.SUCCESS, "");
        });
    }
}
