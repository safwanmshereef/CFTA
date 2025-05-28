<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class StatusSeeder extends Seeder
{
    public function run()
    {
        DB::table('statuses')->insert([
            ['name' => 'Active','created_at' => now(),'updated_at' => now()],
            ['name' => 'Pending','created_at' => now(),'updated_at' => now()],
            ['name' => 'Deactive','created_at' => now(),'updated_at' => now()],
        ]);
    }
}