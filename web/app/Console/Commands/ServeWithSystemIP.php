<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;

class ServeWithSystemIP extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'serve:ip {port=8080}';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Serve the application on the system IP address';

    /**
     * Execute the console command.
     *
     * @return int
     */
    public function handle()
    {
        // Get the system IP address dynamically
        $systemIp = gethostbyname(gethostname());

        // Get the port from the command argument (default is 8000)
        $port = $this->argument('port');

        $this->info("Starting Laravel development server at http://{$systemIp}:{$port}");

        // Run the serve command with the system IP and specified port
        passthru("php artisan serve --host={$systemIp} --port={$port}");
    }
}
