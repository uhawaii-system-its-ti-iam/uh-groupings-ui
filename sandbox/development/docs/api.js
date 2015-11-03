YUI.add("yuidoc-meta", function(Y) {
   Y.YUIDoc = { meta: {
    "classes": [
        "AboutController",
        "AppController",
        "AppService",
        "AuthenticationConfig",
        "AuthenticationController",
        "AuthenticationModel",
        "AuthenticationService",
        "DesignateController",
        "DevelopConfig",
        "FeedbackController",
        "Footer",
        "FooterController",
        "GroupingController",
        "Header",
        "HeaderController",
        "ListNavigation",
        "ListNavigationController",
        "ListNavigationService",
        "Localize",
        "LocationService",
        "MembershipController",
        "PageLoader",
        "ProtectResolver",
        "PublicResolver",
        "Reveal",
        "TranslationConfig",
        "TranslationModel",
        "TranslationResolver",
        "TranslationService"
    ],
    "modules": [
        "app",
        "app.AppController",
        "app.AppService",
        "components",
        "components.footer",
        "components.footer.FooterController",
        "components.footer.uhgFooter",
        "components.header",
        "components.header.HeaderController",
        "components.header.uhgHeader",
        "components.list-navigation",
        "components.list-navigation.ListNavigationController",
        "components.list-navigation.ListNavigationService",
        "components.list-navigation.uhgListNavigation",
        "plugins",
        "routes",
        "routes.about",
        "routes.about.AboutController",
        "routes.designate",
        "routes.designate.DesignateController",
        "routes.feedback",
        "routes.feedback.FeedbackController",
        "routes.grouping",
        "routes.grouping.GroupingController",
        "routes.membership",
        "routes.membership.MembershipController",
        "stack",
        "stack.authentication",
        "stack.authentication.AuthenticationConfig",
        "stack.authentication.AuthenticationController",
        "stack.authentication.AuthenticationModel",
        "stack.authentication.AuthenticationService",
        "stack.authentication.ProtectResolver",
        "stack.authentication.PublicResolver",
        "stack.develop",
        "stack.develop.DevelopConfig",
        "stack.i18n",
        "stack.i18n.TranslationConfig",
        "stack.i18n.TranslationModel",
        "stack.i18n.TranslationResolver",
        "stack.i18n.TranslationService",
        "stack.i18n.localize",
        "stack.i18n.stackLocalize",
        "stack.location",
        "stack.location.LocationService",
        "stack.page-loader",
        "stack.page-loader.stackPageLoader",
        "stack.reveal",
        "stack.reveal.stackReveal",
        "templates"
    ],
    "allModules": [
        {
            "displayName": "app",
            "name": "app",
            "description": "The app module is the top-level application module."
        },
        {
            "displayName": "app.AppController",
            "name": "app.AppController",
            "description": "The AppController is the top-most controller for the application."
        },
        {
            "displayName": "app.AppService",
            "name": "app.AppService",
            "description": "The AppService houses application-level properties and methods."
        },
        {
            "displayName": "components",
            "name": "components",
            "description": "Module definition houses all application-specific components.\nApplication components consist of global widgets or implementations\nthat can be applied to multiple locations within the application."
        },
        {
            "displayName": "components.footer",
            "name": "components.footer",
            "description": "Module definition houses footer implementation."
        },
        {
            "displayName": "components.footer.FooterController",
            "name": "components.footer.FooterController",
            "description": "The FooterController houses state and view logic\nfor the footer directive."
        },
        {
            "displayName": "components.footer.uhgFooter",
            "name": "components.footer.uhgFooter",
            "description": "The Footer directive houses the application footer implementation."
        },
        {
            "displayName": "components.header",
            "name": "components.header",
            "description": "Module definition houses header implementation."
        },
        {
            "displayName": "components.header.HeaderController",
            "name": "components.header.HeaderController",
            "description": "The HeaderController houses state and view logic\nfor the header directive."
        },
        {
            "displayName": "components.header.uhgHeader",
            "name": "components.header.uhgHeader",
            "description": "The Header directive houses the application header implementation."
        },
        {
            "displayName": "components.list-navigation",
            "name": "components.list-navigation",
            "description": "Module definition houses list navigation implementation."
        },
        {
            "displayName": "components.list-navigation.ListNavigationController",
            "name": "components.list-navigation.ListNavigationController",
            "description": "The ListNavigationController houses state and view logic\nfor the ListNavigation directive."
        },
        {
            "displayName": "components.list-navigation.ListNavigationService",
            "name": "components.list-navigation.ListNavigationService",
            "description": "The ListNavigationService provides methods and parsing functionality\nfor the ListNavigation directive."
        },
        {
            "displayName": "components.list-navigation.uhgListNavigation",
            "name": "components.list-navigation.uhgListNavigation",
            "description": "The ListNavigation directive houses the navigation for the application."
        },
        {
            "displayName": "plugins",
            "name": "plugins",
            "description": "The plugins module houses module references\nto third-party angular-based tools and plugins."
        },
        {
            "displayName": "routes",
            "name": "routes",
            "description": "Module definition houses all application routes."
        },
        {
            "displayName": "routes.about",
            "name": "routes.about",
            "description": "Module definition for the about module. The about module manages\nthe application's top-level about view (i.e., /about)."
        },
        {
            "displayName": "routes.about.AboutController",
            "name": "routes.about.AboutController",
            "description": "The AboutController manages view logic rendered to the\nabout.html template for the /about route."
        },
        {
            "displayName": "routes.designate",
            "name": "routes.designate",
            "description": "Module definition for the designate module. The designate module manages\nthe application's top-level designate view (i.e., /designate)."
        },
        {
            "displayName": "routes.designate.DesignateController",
            "name": "routes.designate.DesignateController",
            "description": "The DesignateController manages view logic rendered to the\ndesignate.html template for the /designate route."
        },
        {
            "displayName": "routes.feedback",
            "name": "routes.feedback",
            "description": "Module definition for the feedback module. The feedback module manages\nthe application's top-level feedback view (i.e., /feedback)."
        },
        {
            "displayName": "routes.feedback.FeedbackController",
            "name": "routes.feedback.FeedbackController",
            "description": "The FeedbackController manages view logic rendered to the\nfeedback.html template for the /feedback route."
        },
        {
            "displayName": "routes.grouping",
            "name": "routes.grouping",
            "description": "Module definition for the grouping module. The grouping module manages\nthe application's top-level grouping view (i.e., /grouping)."
        },
        {
            "displayName": "routes.grouping.GroupingController",
            "name": "routes.grouping.GroupingController",
            "description": "The GroupingController manages view logic rendered to the\ngrouping.html template for the /grouping route."
        },
        {
            "displayName": "routes.membership",
            "name": "routes.membership",
            "description": "Module definition for the membership module. The membership module manages\nthe application's top-level membership view (i.e., /membership)."
        },
        {
            "displayName": "routes.membership.MembershipController",
            "name": "routes.membership.MembershipController",
            "description": "The MembershipController manages view logic rendered to the\nmembership.html template for the /membership route."
        },
        {
            "displayName": "stack",
            "name": "stack",
            "description": "Module definition houses collection of stack assets.\nStack assets consist of implementations that are\ngeneric in nature and can be used across multiple\napplications."
        },
        {
            "displayName": "stack.authentication",
            "name": "stack.authentication",
            "description": "Module definition houses the authentication feature."
        },
        {
            "displayName": "stack.authentication.AuthenticationConfig",
            "name": "stack.authentication.AuthenticationConfig",
            "description": "The AuthenticationConfig is a configuration implementation leveraged\nby the authentication module. The AuthenticationConfig implementation\nprovides configuration settings to the Authentication module."
        },
        {
            "displayName": "stack.authentication.AuthenticationController",
            "name": "stack.authentication.AuthenticationController",
            "description": "The AuthenticationController aids in the protecting and locking down\nof authenticated routes. Unauthorized requests are redirected to a\nconfigurable unauthenticated route."
        },
        {
            "displayName": "stack.authentication.AuthenticationModel",
            "name": "stack.authentication.AuthenticationModel",
            "description": "The AuthenticationModel houses CRUD-based methods used to\nauthenticate users."
        },
        {
            "displayName": "stack.authentication.AuthenticationService",
            "name": "stack.authentication.AuthenticationService",
            "description": "The AuthenticationService wraps the AuthenticationModel and houses a\ncollection of methods used to manage the authentication of users."
        },
        {
            "displayName": "stack.authentication.ProtectResolver",
            "name": "stack.authentication.ProtectResolver",
            "description": "The ProtectResolver service should only be applied to routes that require\nauthentication. This service examines the authentication status of the\ncurrent user. If a user is authenticated they are allowed to view the\nroute. If a user is unauthenticated they are directed back to a configured\nunauthenticated route."
        },
        {
            "displayName": "stack.authentication.PublicResolver",
            "name": "stack.authentication.PublicResolver",
            "description": "The PublicResolver service should only be applied to publicly-facing routes.\nThis service examines and returns the authentication status of the current user.\nThe user status is then leveraged to show/hide areas within globally-accessible\ncomponents such as the header or sidebar that require authentication to be seen."
        },
        {
            "displayName": "stack.develop",
            "name": "stack.develop",
            "description": "Module definition houses develop feature."
        },
        {
            "displayName": "stack.develop.DevelopConfig",
            "name": "stack.develop.DevelopConfig",
            "description": "The DevelopConfig provides adopting implementations with a 'develop'\nconfiguration option. Depending upon the implementation, it is sometimes\nuseful to understand whether or not the application is in a 'develop' or\n'production' mode. DevelopConfig provides the .setDevelop(flag) method and\nshould be configured within the compiled app.html page.\n\nSee /clientserver/views/app.html for example."
        },
        {
            "displayName": "stack.i18n",
            "name": "stack.i18n",
            "description": "Module definition houses the i18n feature."
        },
        {
            "displayName": "stack.i18n.localize",
            "name": "stack.i18n.localize",
            "description": "The Localize filter takes a token and returns a translated\nstring using the Translation service. This method is more\nprocessor-intensive given the number of times a filter is\ncalled and should be used sparingly. If possible, leverage\nthe stackLocalize directive instead."
        },
        {
            "displayName": "stack.i18n.stackLocalize",
            "name": "stack.i18n.stackLocalize",
            "description": "The Localize directive takes a token and returns a translated\nstring using the Translation service and injects the translated\nstring into the DOM element."
        },
        {
            "displayName": "stack.i18n.TranslationConfig",
            "name": "stack.i18n.TranslationConfig",
            "description": "The TranslationConfig is a configuration implementation leveraged\nby the i18n implementation."
        },
        {
            "displayName": "stack.i18n.TranslationModel",
            "name": "stack.i18n.TranslationModel",
            "description": "The TranslationModel houses CRUD-based methods used to\nretrieve and manage localized strings."
        },
        {
            "displayName": "stack.i18n.TranslationResolver",
            "name": "stack.i18n.TranslationResolver",
            "description": "The TranslationResolver service loads a map of strings based\nupon the locale reported by the browser."
        },
        {
            "displayName": "stack.i18n.TranslationService",
            "name": "stack.i18n.TranslationService",
            "description": "The TranslationService wraps the TranslationModel and provides\nconvenience methods for managing the the translation of strings\nbased upon different locales."
        },
        {
            "displayName": "stack.location",
            "name": "stack.location",
            "description": "Module definition houses the location feature."
        },
        {
            "displayName": "stack.location.LocationService",
            "name": "stack.location.LocationService",
            "description": "The LocationService extends the angular $location service\nand the ui router $state service. It provides additional\nurl parsing methods and location functionality."
        },
        {
            "displayName": "stack.page-loader",
            "name": "stack.page-loader",
            "description": "Module definition houses the PageLoader feature."
        },
        {
            "displayName": "stack.page-loader.stackPageLoader",
            "name": "stack.page-loader.stackPageLoader",
            "description": "The PageLoader directive provides a spinning loading icon\nas well as a container overlay that the covers underlying\nDOM elements."
        },
        {
            "displayName": "stack.reveal",
            "name": "stack.reveal",
            "description": "Module definition houses the reveal feature."
        },
        {
            "displayName": "stack.reveal.stackReveal",
            "name": "stack.reveal.stackReveal",
            "description": "The Reveal directive prevents UI flicker from happening\nwhen an application is first loaded. This implementation\nis different then using ng-cloak. The ng-clock directive\nwill prevent the angular brackets from rendering to end\nusers. The Reveal directive prevents a flicker caused\nby css and js resources from loading at different times."
        },
        {
            "displayName": "templates",
            "name": "templates",
            "description": "The templates module houses all of the application's templates\nusing the $templateCache service. The $templateCache service is\nleveraged to reduce the number of http requests this application\nmust make when retrieving templates. If a template is stored in\nthe $templateCache the application will not make a request for\nthe template in question."
        }
    ]
} };
});