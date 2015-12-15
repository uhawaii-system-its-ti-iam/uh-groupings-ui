# UH Groupings
* * *
The UH Groupings repository houses a mobile-friendly front-end for the University of Hawaii's Grouper implementation. The UH Groupings application is meant to consume backend API calls provided by the laravel back-end, that ships with this code base. This document outlines how to setup the UH Groupings application (i.e., both the front-end and back-end). Please note, that as of this writing, the laravel back-end is a stubbed implementation used to stand-up the front-end. Additional work and updates to the UH Groupings application will be handled by the University of Hawaii.

## Prerequisites
* * *
The UH Groupings application leverages the [laravel](http://laravel.com/) framework as well as the [node.js](http://nodejs.org/) library for its implementation. The UH Groupings application requires PHP v5.5.9+, node v5.1.0 and npm v3.3.12.

## Project Setup
* * *
To setup UHGroupings, clone the [uhgroupings](https://github.com/Unicon/uhgroupings) git repository.
Open a terminal window and navigate to the root of your project.

    cd path/to/uhgroupings

Run the git clone command.

    git clone git@github.com:Unicon/uhgroupings.git

### Setup Laravel Back-end
* * *
#### Install Composer (System Wide)
Laravel utilizes Composer to manage its dependencies. Open a terminal window and navigate to the root of your project.

        cd path/to/uhgroupings

Run the below commands.
    
    curl -sS https://getcomposer.org/installer | php
    mv composer.phar /usr/local/bin/composer

You will now be able to run the composer command from anywhere on the server. If the above fails due to permissions you may need to execute the move command using sudo.

    sudo mv composer.phar /usr/local/bin/composer

Please refer to the [Composer Documentation](https://getcomposer.org/doc/00-intro.md) for more detailed instructions.

####  Install Composer Dependencies
Open a terminal window and navigate to the root of your project.

    cd path/to/uhgroupings

Run the below command.

    composer install

### Setup Angular Front-end
* * *

#### Install Node & NPM
The uhgroupings application leverages [node.js](http://nodejs.org/) for its development server. IMPORTANT: The development server is a mechanism by which the front-end mocks API calls during development. The development server is not utilized in a production context.

The uhgroupings application also makes use of npm, a package manager for installing node modules. The latest releases of node ships with npm so only a node installation is required. Node offers platform installers for both Windows and Mac OSX. They also offer binaries for Windows, Mac OSX and Linux systems. Visit the [download](https://nodejs.org/en/download/) page for more information.

To verify your node installation, open a terminal and enter the node -v command. Your version should match v5.1.0

    node -v

To verify your npm installation, open a terminal and enter the npm -v command. Your version should match v3.3.12.

    npm -v

#### Install Node Modules
Once node and npm are installed you will need to install all of the node modules leveraged by uhgroupings. You can view all of the module dependencies by examining the package.json file located under the *uhgroupings/sandbox* directory.

Open a terminal window and navigate to your application's *sandbox* directory.

    cd path/to/uhgroupings/sandbox

This application relies on Grunt.js for development tasks and compilation. You may encounter a warning about installing grunt with the -g parameter. The -g installs grunt globally so it is accessible across all projects.

    npm install -g grunt

Run the npm install command. (You must run the npm install command in the same directory that contains the package.json file).

    npm install

Depending upon your system permissions, you may need to run the npm install as root.

    sudo npm install

Once complete, the *node_modules* directory, containing all of your project's node modules, will be added to your project.

#### Build & Compile Front-end
Once everything is installed you will need to build the angular front-end.

Open a terminal window and navigate to your application's *sandbox* directory.

    cd path/to/uhgroupings/sandbox

Run the default grunt command. You need to also add the --force flag. This forces grunt to clean the /public directory, which lives outside of the grunt implementation. Setting the --force flag tells grunt that it has permission to traverse your project directory outside of the directory in which it was defined.

    grunt --force

#### Launch Application
To preview the application using the Larvel framework, open a terminal window and navigate to the root of your project.

    cd path/to/uhgroupings

Run the below command.

    php artisan serve
    
Open a browser window and navigate to *http://localhost:8000*. This will have the affect of launching the application with the **PHP-based Larvel framework**. The framework will serve up files under the */public* directory. The front-end assets that live within the */public* directory are copied from the */sandbox/production* directory, which are compiled and copied from the */sandbox/development* directory when the **grunt --force** command is issued.

## Understanding the Project Architecture
***
TBA
### Working with Node Development Server
TBA
### Generate JavaScript Documentation
The UH Groupings JavaScript implementation has been documented using the [yuiDoc](http://yui.github.io/yuidoc/) tool. This allows us to render out a project API website that describes all JavaScript functionality. To generate JavaScript documentation, please follow the below steps:

Open a terminal window and navigate to your application's *sandbox* directory.

    cd path/to/uhgroupings/sandbox
    
Run the *grunt docs* command to generate documentation.

    grunt docs
    
All documentation assets are ouput to the */docs* directory. When running the UH Groupings *Node Development Server* you can preview the documentation by opening a browser and navigating to *http://localhost:4000/docs* route. As of this writing the documentation is only rendered to *port 4000*, which represents the code base in *development* mode. In addition, the */docs* directory is not being tracked by git and has been added to the .gitignore file since it is a compiled asset.

### Enable Real-Time Markup, CSS & JavaScript Watches & Linters
The UH Groupings application ships with **code watchers** for the application's HTML, CSS and JavaScript development. The watchers are executed via a terminal window and alert the developer when specific implementations do not adhere to predefined coding standards and practices. The watchers can be thought of as pseudo compilers for front-end developers.

To enable the main **HTML watcher** on the application's **index file**, open a separate terminal window and navigate to your application's *sandbox* directory.

    cd path/to/uhgroupings/sandbox
    
Run the **grunt index.watcher** command.

    grunt index.watcher
    
To enable the **CSS watcher** on the application's **.less files**, open a separate terminal window and navigate to your application's *sandbox* directory.

    cd path/to/uhgroupings/sandbox
    
Run the **grunt less.watcher** command.

    grunt less.watcher
    
To enable the **JavaScript watcher** on the application's **.js files**, open a separate terminal window and navigate to your application's *sandbox* directory.

    cd path/to/uhgroupings/sandbox
    
Run the **grunt js.watcher** command.

    grunt js.watcher

Note: To stop any of the above **watch** operations you can type **Crtl + C** in the terminal window.

### Execute JavaScript Unit Tests
The UH Groupings code base ships with the ability to execute JavaScript Unit Tests. To execute JavaScript Unit Tests, please follow the below steps:

Open a terminal window and navigate to your application's *sandbox* directory.

    cd path/to/uhgroupings/sandbox
    
Run the *grunt test* command to start the JavaScript Unit Tests.
    
    grunt test

All tests are executed within the context of the terminal window. The UH Groupings project leverages [Karma](http://karma-runner.github.io/0.12/index.html), [Jasmine](http://jasmine.github.io/) and [PhantomJS](http://phantomjs.org/) for its Unit Testing Framework. As a point of clarification, the code base ships with the ability to run JavaScript Unit Tests, but, due to time constraints, only provides a single implementation example.

As an example, the *TranslationService.spec.js* is used to unit-test the *TranslationService.js* implementation. For detailed instructions, as a starting point, on JavaScript Unit Testing, please checkout out [Angular's Developer Guide on Unit Testing](https://docs.angularjs.org/guide/unit-testing).

    path/to/uhgroupings/sandbox/development/js/src/stack/i18n

## Licensing
***
### Licensed Software
#### Angular 1.4
* Site: [angular](http://angularjs.org/)
* License: [License](https://github.com/angular/angular.js/blob/master/LICENSE)

#### Angular Cache
* Site: [angular-cache](http://jmdobry.github.io/angular-cache/)
* License: [License](https://github.com/jmdobry/angular-cache/blob/master/LICENSE)

#### Angular UI
* Site: [angular-ui](http://angular-ui.github.io/)
* License: [License](https://github.com/angular-ui/bootstrap/blob/master/LICENSE)

#### UI Router
* Site: [ui-router](https://github.com/angular-ui/ui-router)
* License: [License](https://github.com/angular-ui/ui-router/blob/master/LICENSE)

#### jQuery
* Site: [jquery](https://jquery.org/)
* License: [License](https://jquery.org/license/)

#### jQuery Cookie
* Site: [jquery-cookie](https://github.com/carhartl/jquery-cookie)
* License: [License](http://opensource.org/licenses/MIT)

#### Underscore
* Site: [underscore](http://underscorejs.org/)
* License: [License](http://opensource.org/licenses/MIT)

#### Font Awesome
* Site: [font-awesome](http://fortawesome.github.io/Font-Awesome/)
* License: [License](http://fortawesome.github.io/Font-Awesome/license/)

#### Less
* Site: [less](http://lesscss.org/)
* License: [License](http://lesscss.org/#license-faqs)

#### Node
* Site: [node](http://nodejs.org/)
* License: [License](https://raw.github.com/joyent/node/v0.10.26/LICENSE)

#### Grunt
* Site: [grunt](http://gruntjs.com/)
* License: [License](https://github.com/gruntjs/grunt/blob/master/LICENSE-MIT)

#### Karma
* Site: [karman](http://karma-runner.github.io/0.12/index.html)
* License: [License](http://opensource.org/licenses/MIT)