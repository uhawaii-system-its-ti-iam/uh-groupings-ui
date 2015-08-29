<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;

class SetupGrouper extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'setup:grouper';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Performs an environment setup for your this instance of Grouper.';

    /**
     * Holder for the environment prompt value
     * @var null
     */
    protected $environment = 'development';

    /**
     * Required version of PHP
     * This should match the minimum version required for this particular
     * version of Laravel.
     */
    protected $requiredPhpVersion = '5.5.9';

    /**
     * Database information
     */
    protected $databaseHost = "localhost";
    protected $databaseName = "homestead";
    protected $databaseUsername = "homestead";
    protected $databasePassword = "secret";

    /**
     * Default values
     */
    protected $appKey = null;
    protected $debug = true;
    protected $continue = false;

    /**
     * Create a new command instance.
     */
    public function __construct()
    {
        parent::__construct();
        $this->appKey = md5(str_random(32));
    }

    /**
     * Execute the console command.
     *
     * @return mixed
     */
    public function handle()
    {
        $this->welcomeMessage();

        $this->phpVersionCheck();

        if ( $this->fileCheck() ) {
            $this->continue = $this->overwritePrompt();
        } else {
            $this->continue = true;
        }

        if ($this->continue) {
            $this->environmentPrompt();
            $this->databasePrompt();
            $this->writeFile();
        } else {
            $this->info('Finished! Please continue on with setting up the UI.');
        }

    }

    /**
     * Display a welcome message
     */
    private function welcomeMessage() {
        $this->info('');
        $this->info('Welcome to the UH Grouper Setup Wizard');
        $this->info('--------------------------------------');
        $this->info('');
    }

    private function phpVersionCheck() {

        if (version_compare(PHP_VERSION, $this->requiredPhpVersion) < 0) {
            $this->error('                                                                ');
            $this->error(' Opps, Something went wrong!                                    ');
            $this->error('=+==============================================================');
            $this->error(' It appears that you have an older version of PHP than what is  ');
            $this->error(' required to run this application.                              ');
            $this->error('                                                                ');
            $this->error(' You currently have v' . PHP_VERSION . ' installed. We require v' . $this->requiredPhpVersion . ' or     ');
            $this->error(' greater. Please upgrade and re-run this setup.                 ');
            $this->error('                                                                ');
            $this->info('');
            die();
        }
    }

    /**
     * Prompts for environment choice and set the appropriate variable for
     * use later
     */
    private function environmentPrompt() {
        $this->environment = strtolower($this->choice('Which environment are you using?', ['Development', 'Production'], false));

        $this->debug = ( $this->environment == 'development' ) ? 'true' : 'false';

    }

    /**
     * Prompt the user if they want to setup a database.
     * If no, then continue, otherwise prompt for database information
     */
    private function databasePrompt() {
        if ($this->confirm('Do you wish to use a Database? [y|N]')) {
            //
            $this->databaseName = $this->ask('What is the Database name?');
            $this->databaseHost = $this->ask('What is the Database hostname?');
            $this->databaseUsername = $this->ask('What is the Database username?');
            $this->databasePassword = $this->secret('What is the Database password? (note: you will not see anything while you type your password)');
        }
    }


    /**
     * Check to see if the .env file is already present
     */
    private function fileCheck() {
        return file_exists( '.env' );
    }

    /**
     * Display a prompt about overwriting existing file
     * @return bool
     */
    private function overwritePrompt() {
        return $this->confirm('We have detected that there is already a .env file, and will be overwritten. Do you wish to proceed? [y|N]');
    }

    /**
     * Create the .env file based on default and user supplied values
     */
    private function writeFile() {
        $myfile = fopen(".env", "w") or die("Unable to open file!");


        $txt = <<<HEREDOC
APP_ENV=$this->environment
APP_DEBUG=$this->debug
APP_KEY=$this->appKey

DB_HOST=$this->databaseHost
DB_DATABASE=$this->databaseName
DB_USERNAME=$this->databaseUsername
DB_PASSWORD=$this->databasePassword

CACHE_DRIVER=file
SESSION_DRIVER=file
QUEUE_DRIVER=sync

MAIL_DRIVER=smtp
MAIL_HOST=mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=null
MAIL_PASSWORD=null
MAIL_ENCRYPTION=null
HEREDOC;

        fwrite($myfile, $txt);
        fclose($myfile);

        return;
    }
}
