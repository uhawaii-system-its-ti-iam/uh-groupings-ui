# UH Groupings #

> PHP v5.5.9 or greater is required to run this application.

## Setup
[](#setup)
To setup UHGroupings, you need to clone the Git repo with the following command:
`git clone git@github.com:Unicon/uhgroupings.git`

### Install Composer (system wide)
[](#composer)

#### Composer Quick instructions
Laravel utilizes Composer to manage its dependencies. Without composer installed locally and on the server environments Laravel will not be able to work. Run the following commands:

`$ curl -sS https://getcomposer.org/installer | php`

then

`$ mv composer.phar /usr/local/bin/composer`

You will now be able to run the composer command from anywhere on the server.

**Note**: *If the above fails due to permissions, run the mv line again with sudo*

Refer to the [Composer Documentation](https://getcomposer.org/doc/00-intro.md) for more detailed instructions.


## Backend
[](#backend)
In the application root directory,

- run `composer install` to install all of the Laravel dependicies

<!--- copy .env.example to .env --->

- run `php artisan setup:grouper` and follow the prompts to setup your Laravel envionment.

<!--
### Development
- edit .env and set the APP_ENV to 'development'

### Production
- edit .env and set the APP_ENV to 'development'
-->

## Frontend
[](#frontend)
- change into the 'public' directory
- run `npm install`

### Development
- run `grunt` to to set the code up for development use.

### Production
- run `grunt prod` to run the minification scripts and to set the code up for production use.

### Local Server
If you do not have a local web server installed, you can run `php artisan serve` from the application root to start a simple test server.  To stop the server, type `ctrl-c`.

## Troubleshooting
#### I see a "Welcome" page
Sounds like you did not run the [frontend](#frontend) setup, or there was an error during the setup. Please refer to the [frontend](#frontend) setup instructions and try it again.

#### I still see a "Welcome" page
Contact one of the developers for assistence with troubleshooting this issue.
 
#### I see a blank page
Check the browser console log to see if there were any JavaScript errors.

Check the server error log to see if PHP threw an error.