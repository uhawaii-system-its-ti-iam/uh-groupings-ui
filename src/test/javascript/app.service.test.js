/* global inject */
describe("AppService", () => {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));

    let BASE_URL;
    let dp;
    let httpBackend;
    let onSuccess;
    let onError;
    let timeout;
    let initialPoll;
    let modal;

    beforeEach(inject((dataProvider, _$httpBackend_, _BASE_URL_, _$timeout_, _$window_) => {
        BASE_URL = _BASE_URL_;
        dp = dataProvider;
        httpBackend = _$httpBackend_;
        onSuccess = jasmine.createSpy("onSuccess");
        onError = jasmine.createSpy("onError");
        modal = jasmine.createSpy("modal");
        timeout = _$timeout_;
        initialPoll = 5000;
    }));

    it("should define dataProvider", () => {
        expect(dp).toBeDefined();
    });

    describe("loadData", () => {
        let endpoint;

        beforeEach(() => {
            endpoint = BASE_URL + "/";
        });

        it("should call onSuccess", () => {
            dp.loadData(endpoint, onSuccess, onError);

            httpBackend.expectGET(endpoint).respond(200, "hello");
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith("hello");
            expect(onError).not.toHaveBeenCalled();
        });

        it("should call onError when callError is provided", () => {
            dp.loadData(endpoint, onSuccess, onError);

            httpBackend.expectGET(endpoint).respond(500);
            httpBackend.flush();

            expect(onError).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
        });

        it("should not throw when callError is missing and the request fails", () => {
            const endpointWithoutErrorCallback = `${BASE_URL}missing-error-callback`;
            dp.loadData(endpointWithoutErrorCallback, onSuccess);

            httpBackend.expectGET(endpointWithoutErrorCallback).respond(500);

            expect(() => httpBackend.flush()).not.toThrow();
            expect(onSuccess).not.toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });
    });

    describe("loadDataWithBody", () => {
        let endpoint;
        const requestData = { key: "value" };

        beforeEach(() => {
            endpoint = BASE_URL + "/";
            dp.loadDataWithBody(endpoint, requestData, onSuccess, onError);
        });

        it("should call onSuccess", () => {
            httpBackend.expectPOST(endpoint, requestData).respond(200, "hello");
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith("hello");
            expect(onError).not.toHaveBeenCalled();
        });

        it("should call onError", () => {
            httpBackend.expectPOST(endpoint, requestData).respond(500);
            httpBackend.flush();

            expect(onError).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
        });
    });

    describe("loadDataWithBodyAsync", () => {
        let endpoint;
        const response = { key: 1 };
        const result = "result";

        beforeEach(() => {
            endpoint = BASE_URL;
            dp.loadDataWithBodyAsync(endpoint, response, initialPoll, onSuccess, onError);
        });

        it("should call onSuccess", () => {
            httpBackend.expectPOST(endpoint, response).respond(200, 1);
            httpBackend.flush();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "IN_PROGRESS" });
            timeout.flush();
            httpBackend.flush();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result });
            timeout.flush();
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith(result);
            expect(onError).not.toHaveBeenCalled();
        });

        it("should call onError", () => {
            httpBackend.expectPOST(encodeURI(endpoint), response).respond(500);
            httpBackend.flush();

            expect(onError).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
        });
    });

    describe("loadDataWithBodyRetry", () => {
        let endpoint;
        const response = { key: 1 };
        const result = "result";
        const retries = 2;

        beforeEach(() => {
            endpoint = BASE_URL;
            dp.loadDataWithBodyRetry(endpoint, response, onSuccess, onError, retries);
        });

        it("should call onSuccess", () => {
            httpBackend.expectPOST(encodeURI(endpoint), response).respond(200, 1);
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith(1);
            expect(onError).not.toHaveBeenCalled();
        });

        it("should call onError and retry", () => {
            for (let i = 0; i < retries; i++) {
                httpBackend.expectPOST(encodeURI(endpoint), response).respond(500);
            }

            dp.loadDataWithBodyRetry(endpoint, response, onSuccess, onError, 0);
            httpBackend.flush();

            expect(onSuccess).not.toHaveBeenCalled();
            expect(onError).toHaveBeenCalled();
        });

    });

    describe("updateData", () => {
        let endpoint;

        beforeEach(() => {
            endpoint = BASE_URL + "/";
            dp.updateData(endpoint, onSuccess, onError);
        });

        it("should call onSuccess", () => {
            httpBackend.expectPOST(endpoint).respond(200, "hello");
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith("hello");
            expect(onError).not.toHaveBeenCalled();
        });

        it("should call onError", () => {
            httpBackend.expectPOST(endpoint).respond(500);
            httpBackend.flush();

            expect(onError).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
        });

    });

    describe("updateDataAsync", () => {
        let endpoint;
        const result = "result";

        beforeEach(() => {
            endpoint = BASE_URL + "/";
            dp.updateDataAsync(endpoint, initialPoll, onSuccess, onError);
        });

        it("should call onSuccess", () => {
            httpBackend.expectPOST(encodeURI(endpoint)).respond(200, 1);
            httpBackend.flush();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "IN_PROGRESS" });
            timeout.flush();
            httpBackend.flush();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result });
            timeout.flush();
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith(result);
            expect(onError).not.toHaveBeenCalled();

        });

        it("should call onError", () => {
            httpBackend.expectPOST(encodeURI(endpoint)).respond(500);
            httpBackend.flush();

            expect(onError).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
        });

    });

    describe("updateDataWithBody", () => {
        let endpoint;
        const requestData = { key: "value" };

        beforeEach(() => {
            endpoint = BASE_URL + "/";
            dp.updateDataWithBody(endpoint, requestData, onSuccess, onError);
        });

        it("should call onSuccess", () => {
            httpBackend.expectPUT(encodeURI(endpoint), requestData).respond(200, "hello");
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });

        it("should call onError", () => {
            httpBackend.expectPUT(encodeURI(endpoint), requestData).respond(500);
            httpBackend.flush();

            expect(onSuccess).not.toHaveBeenCalled();
            expect(onError).toHaveBeenCalled();
        });
    });


    describe("updateDataWithBodyAndTimeoutModel", () => {
        let endpoint;
        const requestData = { key: "value" };

        beforeEach(() => {
            endpoint = BASE_URL + "/";
            dp.updateDataWithBodyAndTimeoutModal(endpoint, requestData, onSuccess, onError, modal);
        });

        it("should call onSuccess", () => {
            httpBackend.expectPUT(endpoint, requestData).respond(200, "hello");
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith("hello");
            expect(onError).not.toHaveBeenCalled();
            expect(modal).not.toHaveBeenCalled();
        });

        it("should call onError", () => {
            httpBackend.expectPUT(endpoint, requestData).respond(500);
            httpBackend.flush();

            expect(onSuccess).not.toHaveBeenCalled();
            expect(onError).toHaveBeenCalled();
            expect(modal).not.toHaveBeenCalled();
        });

        it("should call the modal on timeout", () => {
            httpBackend.expectPUT(endpoint, requestData).respond(200);
            timeout.flush();

            expect(modal).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });
    });

    describe("updateDataWithBodyAndTimeoutModalAsync", () => {
        let endpoint;
        const result = "result";

        beforeEach(() => {
            endpoint = BASE_URL + "/";
            dp.updateDataWithBodyAndTimeoutModalAsync(endpoint, result, initialPoll, onSuccess, onError, modal);
        });

        it("should call onSuccess", () => {
            httpBackend.expectPUT(endpoint, result).respond(200, 1);
            httpBackend.flush();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "IN_PROGRESS" });
            timeout.flush();
            httpBackend.flush();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result });
            timeout.flush();
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith(result);
            expect(onError).not.toHaveBeenCalled();
            expect(modal).toHaveBeenCalled();
        });

        it("should call onError", () => {
            httpBackend.expectPUT(endpoint, result).respond(500);
            httpBackend.flush();

            expect(onError).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
            expect(modal).not.toHaveBeenCalled();
        });

        it("should not call the modal when the job completes before the time limit", () => {
            httpBackend.expectPUT(endpoint, result).respond(200, 1);
            httpBackend.flush();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result });
            timeout.flush(initialPoll);
            httpBackend.flush();
            timeout.flush(20000);

            expect(onSuccess).toHaveBeenCalledWith(result);
            expect(modal).not.toHaveBeenCalled();
        });

        it("should not call the modal when polling fails before the time limit", () => {
            httpBackend.expectPUT(endpoint, result).respond(200, 1);
            httpBackend.flush();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(403);
            timeout.flush(initialPoll);
            httpBackend.flush();
            timeout.flush(20000);

            expect(onError).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
            expect(modal).not.toHaveBeenCalled();
        });

        it("should call the modal on timeout", () => {
            httpBackend.expectPUT(endpoint, result).respond(200);
            timeout.flush();

            expect(modal).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });
    });

    describe("polling an async job", () => {
        let endpoint;
        let jobPolled;
        // The waits before each poll sent again after a poll that failed transiently, in a row.
        const retryDelays = [5000, 10000, 20000, 30000, 30000, 30000, 30000, 30000];

        /**
         * Respond with the given response to the poll that is due after the given wait.
         */
        const respondToPoll = (wait, ...response) => {
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(...response);
            timeout.flush(wait);
            httpBackend.flush();
        };

        beforeEach(inject(($rootScope) => {
            endpoint = BASE_URL + "/";
            jobPolled = jasmine.createSpy("jobPolled");
            $rootScope.$on("asyncJobPolled", jobPolled);
            dp.updateDataWithBodyAndTimeoutModalAsync(endpoint, "data", initialPoll, onSuccess, onError, modal);
            httpBackend.expectPUT(endpoint, "data").respond(200, 1);
            httpBackend.flush();
        }));

        it("should broadcast asyncJobPolled for each answered poll, so the idle timeout does not log the user out", () => {
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "IN_PROGRESS" });
            timeout.flush(initialPoll);
            httpBackend.flush();
            expect(jobPolled).toHaveBeenCalledTimes(1);

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result: "result" });
            timeout.flush(5000);
            httpBackend.flush();
            expect(jobPolled).toHaveBeenCalledTimes(2);
        });

        it("should not broadcast asyncJobPolled when a poll fails", () => {
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(403);
            timeout.flush(initialPoll);
            httpBackend.flush();

            expect(jobPolled).not.toHaveBeenCalled();
            expect(onError).toHaveBeenCalled();
        });

        it("should not broadcast asyncJobPolled when a poll fails transiently", () => {
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(503);
            timeout.flush(initialPoll);
            httpBackend.flush();

            expect(jobPolled).not.toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });

        [0, 408, 429, 500, 502, 503, 504].forEach((status) => {
            it(`should poll again after a poll that failed with transient status ${status}, since the job runs on`,
                () => {
                    httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(status);
                    timeout.flush(initialPoll);
                    httpBackend.flush();
                    expect(onError).not.toHaveBeenCalled();

                    httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result: "result" });
                    timeout.flush(5000);
                    httpBackend.flush();

                    expect(onSuccess).toHaveBeenCalledWith("result");
                    expect(onError).not.toHaveBeenCalled();
                });
        });

        [400, 401, 403, 404, 501].forEach((status) => {
            it(`should fail with status ${status}, and stop polling, when a poll fails for good`, () => {
                httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(status);
                timeout.flush(initialPoll);
                httpBackend.flush();
                timeout.flush(20000);

                httpBackend.verifyNoOutstandingRequest();
                expect(onError).toHaveBeenCalledTimes(1);
                expect(onError).toHaveBeenCalledWith(jasmine.objectContaining({ status }));
                expect(onSuccess).not.toHaveBeenCalled();
                expect(modal).not.toHaveBeenCalled();
            });
        });

        it("should wait twice as long to poll again after each poll in a row that failed transiently, up to 30 s", () => {
            respondToPoll(initialPoll, 503);
            retryDelays.slice(0, -1).forEach((retryDelay) => {
                timeout.flush(retryDelay - 1);
                httpBackend.verifyNoOutstandingRequest();
                respondToPoll(1, 503);
            });

            expect(onError).not.toHaveBeenCalled();
        });

        it("should fail with the last failed poll's status once the polls in a row that failed transiently run out",
            () => {
                const statuses = [503, -1, 502, 429, 504, 0, 500, 408, 502];
                respondToPoll(initialPoll, statuses[0]);
                statuses.slice(1).forEach((status, retry) => {
                    expect(onError).not.toHaveBeenCalled();
                    respondToPoll(retryDelays[retry], status);
                });

                expect(onError).toHaveBeenCalledTimes(1);
                expect(onError).toHaveBeenCalledWith(jasmine.objectContaining({ status: 502 }));
                expect(onSuccess).not.toHaveBeenCalled();
                timeout.flush(60000);
                httpBackend.verifyNoOutstandingRequest();
            });

        [
            { status: 503, data: { resultCode: "BACKEND_UNAVAILABLE", message: "Groupings data is unavailable." } },
            { status: 500, data: { resultCode: "FAILURE", message: "Runtime Exception" } }
        ].forEach(({ status, data }) => {
            it(`should fail at once with an error the API answered with (status ${status}), since it is the failure `
                + "of the job, which every later poll would get too", () => {
                respondToPoll(initialPoll, status, data);
                timeout.flush(60000);

                httpBackend.verifyNoOutstandingRequest();
                expect(onError).toHaveBeenCalledTimes(1);
                expect(onError).toHaveBeenCalledWith(jasmine.objectContaining({ status, data }));
                expect(onSuccess).not.toHaveBeenCalled();
                expect(modal).not.toHaveBeenCalled();
            });
        });

        it("should fail with status 404, and stop polling, when the job is gone after a poll that failed transiently "
            + "(e.g. the API restarted meanwhile)", () => {
            respondToPoll(initialPoll, 502);
            respondToPoll(5000, 200, { id: 1, status: "NOT_FOUND", result: "" });
            timeout.flush(60000);

            httpBackend.verifyNoOutstandingRequest();
            expect(onError).toHaveBeenCalledTimes(1);
            expect(onError).toHaveBeenCalledWith(jasmine.objectContaining({ status: 404 }));
            expect(onSuccess).not.toHaveBeenCalled();
        });

        it("should fail for good on a poll that fails for good after polls that failed transiently", () => {
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(503);
            timeout.flush(initialPoll);
            httpBackend.flush();
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(401);
            timeout.flush(5000);
            httpBackend.flush();

            expect(onError).toHaveBeenCalledTimes(1);
            expect(onError).toHaveBeenCalledWith(jasmine.objectContaining({ status: 401 }));
            timeout.flush(20000);
            httpBackend.verifyNoOutstandingRequest();
        });

        it("should fail with status 404, and stop polling, when the API no longer has the job", () => {
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { id: 1, status: "NOT_FOUND", result: "" });
            timeout.flush(initialPoll);
            httpBackend.flush();
            timeout.flush(20000);

            httpBackend.verifyNoOutstandingRequest();
            expect(onError).toHaveBeenCalledWith(jasmine.objectContaining({ status: 404 }));
            expect(onSuccess).not.toHaveBeenCalled();
            expect(jobPolled).not.toHaveBeenCalled();
            expect(modal).not.toHaveBeenCalled();
        });

        it("should poll again after a poll that got no answer, since the job runs on", () => {
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(-1);
            timeout.flush(initialPoll);
            httpBackend.flush();
            expect(onError).not.toHaveBeenCalled();

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result: "result" });
            timeout.flush(5000);
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith("result");
            expect(onError).not.toHaveBeenCalled();
        });

        it("should fail once about 3 minutes of polls in a row got no answer", () => {
            respondToPoll(initialPoll, -1);
            retryDelays.forEach((retryDelay) => {
                expect(onError).not.toHaveBeenCalled();
                respondToPoll(retryDelay, -1);
            });

            expect(onError).toHaveBeenCalledTimes(1);
            expect(onError).toHaveBeenCalledWith(jasmine.objectContaining({ status: -1 }));
            timeout.flush(60000);
            httpBackend.verifyNoOutstandingRequest();
        });

        it("should count only the polls in a row that got no answer, and wait 5 s again after an answered poll", () => {
            respondToPoll(initialPoll, -1);
            retryDelays.slice(0, -1).forEach((retryDelay) => respondToPoll(retryDelay, -1));
            respondToPoll(retryDelays[retryDelays.length - 1], 200, { status: "IN_PROGRESS" });
            respondToPoll(5000, -1);
            respondToPoll(5000, 200, { status: "COMPLETED", result: "result" });

            expect(onError).not.toHaveBeenCalled();
            expect(onSuccess).toHaveBeenCalledWith("result");
        });
    });

    describe("polling the progress of an async job", () => {
        let endpoint;
        let onProgress;

        beforeEach(() => {
            endpoint = BASE_URL + "/";
            onProgress = jasmine.createSpy("onProgress");
            dp.updateDataWithBodyAndTimeoutModalAsync(endpoint, "data", initialPoll, onSuccess, onError, modal,
                onProgress);
            httpBackend.expectPUT(endpoint, "data").respond(200, 1);
            httpBackend.flush();
        });

        it("should pass on the progress of each poll of a job in progress", () => {
            const validating = { phase: "VALIDATING", done: 1000, total: 12412 };
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "IN_PROGRESS", progress: validating });
            timeout.flush(initialPoll);
            httpBackend.flush();
            expect(onProgress).toHaveBeenCalledWith(validating);

            const adding = { phase: "ADDING", done: 250, total: 12284 };
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "IN_PROGRESS", progress: adding });
            timeout.flush(5000);
            httpBackend.flush();
            expect(onProgress).toHaveBeenCalledWith(adding);

            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result: "result" });
            timeout.flush(5000);
            httpBackend.flush();
            expect(onProgress).toHaveBeenCalledTimes(2);
            expect(onSuccess).toHaveBeenCalledWith("result");
        });

        it("should not pass on progress that a job in progress does not report", () => {
            httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "IN_PROGRESS", progress: null });
            timeout.flush(initialPoll);
            httpBackend.flush();

            expect(onProgress).not.toHaveBeenCalled();
        });
    });

    it("should poll a job that reports progress without a progress callback", () => {
        dp.updateDataWithBodyAndTimeoutModalAsync(BASE_URL + "/", "data", initialPoll, onSuccess, onError, modal);
        httpBackend.expectPUT(BASE_URL + "/", "data").respond(200, 1);
        httpBackend.flush();

        httpBackend.expectGET(`${BASE_URL}jobs/1`)
            .respond(200, { status: "IN_PROGRESS", progress: { phase: "ADDING", done: 0, total: 1 } });
        timeout.flush(initialPoll);
        httpBackend.flush();
        httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result: "result" });
        timeout.flush(5000);
        httpBackend.flush();

        expect(onSuccess).toHaveBeenCalledWith("result");
    });
});

describe("asyncJobPoller", () => {

    beforeEach(module("UHGroupingsApp"));
    beforeEach(module("ngMockE2E"));
    // An error thrown in a promise callback is reported as a possibly unhandled rejection: log it, not rethrow it.
    beforeEach(module(($exceptionHandlerProvider) => {
        $exceptionHandlerProvider.mode("log");
    }));

    it("should keep polling a job when handling its progress fails",
        inject((asyncJobPoller, $httpBackend, $timeout, BASE_URL) => {
            const onSuccess = jasmine.createSpy("onSuccess");
            const onError = jasmine.createSpy("onError");
            const onProgress = jasmine.createSpy("onProgress").and.throwError("progress display failed");
            asyncJobPoller(1, onSuccess, onError, undefined, onProgress);

            $httpBackend.expectGET(`${BASE_URL}jobs/1`)
                .respond(200, { status: "IN_PROGRESS", progress: { phase: "ADDING", done: 0, total: 1 } });
            $httpBackend.flush();
            expect(onProgress).toHaveBeenCalled();

            $httpBackend.expectGET(`${BASE_URL}jobs/1`).respond(200, { status: "COMPLETED", result: "result" });
            $timeout.flush(5000);
            $httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith("result");
            expect(onError).not.toHaveBeenCalled();
        }));
});
