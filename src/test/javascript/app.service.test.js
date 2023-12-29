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
        const endpoint = BASE_URL + "/";

        beforeEach(() => {
            dp.loadData(endpoint, onSuccess, onError);
        });

        it("should call onSuccess", () => {
            httpBackend.expectGET(endpoint).respond(200, "hello");
            httpBackend.flush();

            expect(onSuccess).toHaveBeenCalledWith("hello");
            expect(onError).not.toHaveBeenCalled();
        });

        it("should call onError", () => {
            httpBackend.expectGET(endpoint).respond(500);
            httpBackend.flush();

            expect(onError).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
        });
    });

    describe("loadDataWithBody", () => {
        const endpoint = BASE_URL + "/";
        const requestData = { key: "value" };

        beforeEach(() => {
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
        const endpoint = BASE_URL + "/";
        const result = "result";

        beforeEach(() => {
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
        const endpoint = BASE_URL + "/";
        const requestData = { key: "value" };

        beforeEach(() => {
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
        const endpoint = BASE_URL + "/";
        const requestData = { key: "value" };

        beforeEach(() => {
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
        const endpoint = BASE_URL + "/";
        const result = "result";

        beforeEach(() => {
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

        it("should call the modal on timeout", () => {
            httpBackend.expectPUT(endpoint, result).respond(200);
            timeout.flush();

            expect(modal).toHaveBeenCalled();
            expect(onSuccess).not.toHaveBeenCalled();
            expect(onError).not.toHaveBeenCalled();
        });
    });
});
