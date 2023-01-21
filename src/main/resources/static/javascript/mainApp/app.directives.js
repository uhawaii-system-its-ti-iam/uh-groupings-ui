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

        // element.attr("data-placement", attrs["tooltipPosition"]);
        // this line will make the tooltip nonformatted apparently

    };
});
