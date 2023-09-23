/* global inject */

describe("Directives", () => {

    beforeEach(module("UHGroupingsApp"));

    let compile;
    let scope;

    beforeEach(inject(($compile, $rootScope) => {
        compile = $compile;
        scope = $rootScope;
    }));

    describe("tooltipOnTruncate", () => {

        beforeEach(function () {
            $.fn.triggerSVGEvent = function (eventName) {
                let event = new Event(eventName, {"bubbles": true, "cancelable": true});
                this[0].dispatchEvent(event);
                return $(this);
            };
        });

        it("should open a tooltip", inject(() => {
            let text = "This is a long piece of text that will truncate/overflow its div";

            let element = document.createElement("div");
            element.style.width = "100px";
            element.setAttribute("tooltip-on-truncate", text);
            element.textContent = text;
            document.body.appendChild(element);

            element = compile(element)(scope);
            scope.$digest();

            expect(element.attr("tooltip-on-truncate")).toBe(text);
            expect(element.attr("data-original-title")).toBe(text);
            expect(element.attr("data-boundary")).toBe("window");
            expect(element.attr("data-delay")).toBe("{\"show\": 300}");

            spyOn($.fn, "tooltip");
            $(element).triggerSVGEvent("mouseover");
            expect($.fn.tooltip).toHaveBeenCalledWith("toggle");
            $(element).remove();
        }));

        it("should not open a tooltip", inject(() => {
            let text = "Short text that will not overflow";

            let element = document.createElement("div");
            element.style.width = "100px";
            element.setAttribute("tooltip-on-truncate", text);
            element.textContent = text;
            document.body.appendChild(element);

            element = compile(element)(scope);
            scope.$digest();

            expect(element.attr("tooltip-on-truncate")).toBe(text);
            expect(element.attr("data-original-title")).toBe(text);
            expect(element.attr("data-boundary")).toBe("window");
            expect(element.attr("data-delay")).toBe("{\"show\": 300}");

            spyOn($.fn, "tooltip");
            $(element).triggerSVGEvent("mouseover");
            expect($.fn.tooltip).not.toHaveBeenCalledWith("toggle");
            $(element).remove();
        }));
    });

    describe("tooltip", () => {

        beforeEach(function () {
            $.fn.triggerSVGEvent = function (eventName) {
                let event = new Event(eventName, {"bubbles": true, "cancelable": true});
                this[0].dispatchEvent(event);
                return $(this);
            };
        });

        it("should check tooltip has all the attributes from the tooltip directive", inject(() => {
            let text = "This is a tooltip";

            let element = document.createElement("div");
            element.style.width = "100px";
            element.setAttribute("tooltip", text);
            element.textContent = text;
            document.body.appendChild(element);

            element = compile(element)(scope);
            scope.$digest();

            expect(element.attr("tooltip")).toBe(text);
            expect(element.attr("data-toggle")).toBe("tooltip");
            expect(element.attr("data-boundary")).toBe("window");
            expect(element.attr("data-original-title")).toBe(text);
            expect(element.attr("data-delay")).toBe("{\"show\": 300}");

            spyOn($.fn, "tooltip");
            $(element).triggerSVGEvent("mouseover");
            expect($.fn.tooltip).toHaveBeenCalledWith("toggle");
            $(element).triggerSVGEvent("mouseout");
            expect($.fn.tooltip).toHaveBeenCalledWith("dispose");
            expect(element.attr("aria-describedby")).toBe(undefined);
            $(element).remove();

        }));
    });
});



