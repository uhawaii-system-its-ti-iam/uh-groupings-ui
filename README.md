# UH Groupings
* * *
The UH Groupings repository houses a mobile-friendly front-end for the University of Hawaii's Grouper implementation. The UH Groupings application is meant to consume backend API calls provided by the laravel back-end, that ships with this code base. This document outlines how to setup the UH Groupings application (i.e., both the front-end and back-end). Please note, that as of this writing, the laravel back-end is a stubbed implementation used to stand-up the front-end. Additional work and updates to the UH Groupings application will be handled by the University of Hawaii.

## Prerequisites
* * *
The UH Groupings application leverages the [laravel](http://laravel.com/) framework as well as the [node.js](http://nodejs.org/) library for its implementation. The UH Groupings application requires PHP v5.5.9+, node v0.12.7+ and npm v2.11.3+.

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

To verify your node installation, open a terminal and enter the node -v command.

    node -v

To verify your npm installation, open a terminal and enter the npm -v command.

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

Run the default grunt command.

    grunt

#### Launch Application
To preview the application using the Larvel framework, open a terminal window and navigate to the root of your project.

    cd path/to/uhgroupings

Run the below command.

    php artisan serve

## Understanding the Project Architecture
***
TBA
### Working with Node Development Server
TBA
### Generate JavaScript Documentation
TBA
### Enable Real-Time JavaScript Linter
TBA
### Execute JavaScript Unit Tests
TBA

## Licensing
***
### Licensed Software
#### Angular
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