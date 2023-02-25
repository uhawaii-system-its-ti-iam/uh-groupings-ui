/* global UHGroupingsApp */

UHGroupingsApp.directive("fileModel", ["$parse", function ($parse) {
    return {
        restrict: "A",
        link(scope, element, attrs) {
            let model = $parse(attrs.fileModel);
            let modelSetter = model.assign;

            element.bind("change", function () {
                scope.$apply(function () {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

UHGroupingsApp.directive("tooltipOnTruncate", function () {
    return {
        restrict: "A",
        scope: {
            title: "@tooltipOnTruncate"
        },
        link(scope, element) {
            element.attr("data-original-title", scope.title);
            element.attr("data-boundary", "window");
            element.attr("data-delay", "{\"show\": 300}");

            element.on("mouseover", function () {
                if (element[0].offsetWidth < element[0].scrollWidth) {
                    $(element).tooltip("toggle");
                } else {
                    $(element).tooltip("dispose");
                }
            });
        }
    };
});

/**
 * Directive for tooltips. Applies the described attributes to a tooltip.
 *
 * The line- element.attr("data-placement", attrs["tooltipPosition"]); will make the tooltip nonformatted.
 * Use Bootstrap's data-placement attribute to change placement of tooltip.
 *
 * For example:
 *  Adding the following to an html element will add a tooltip with a top placement that when hovered over, has the text "Click here":
 *  tooltip="Click here" data-placement="top"
 *
 * @param title The text that will display for the tooltip. If no title parameter is given, will default to the element's title.
 * @param data-placement Determines the placement of the tooltip
 */
UHGroupingsApp.directive("tooltip", function () {
    return {
        restrict: "A",
        scope: {
            title: "@tooltip"
        },
        link(scope, element) {
            element.attr("data-toggle","tooltip");
            element.attr("data-original-title", scope.title);
            element.attr("data-boundary", "window");
            element.attr("data-delay", "{\"show\": 300}");

            element.on("mouseover", function () {
                $(element).tooltip("toggle");
            });
            element.on("mouseout", function () {
                $(element).tooltip("dispose");
            });

        }
    };
});
