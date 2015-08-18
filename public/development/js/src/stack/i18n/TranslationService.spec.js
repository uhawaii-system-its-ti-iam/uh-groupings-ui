describe('TranslationService', function () {
    'use strict';

    // Define.
    var service;

    // Load module.
    beforeEach(module('stack.i18n.TranslationService'));

    // Inject dependencies.
    beforeEach(inject(function (TranslationService) {
        service = TranslationService;
    }));

    // Tests.
    it('should be defined', function () {
        expect(service).toBeDefined();
    });
});
