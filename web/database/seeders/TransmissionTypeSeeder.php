<?php
namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class TransmissionTypeSeeder extends Seeder
{
    public function run()
    {
        DB::table('transmission_types')->insert([
            ['name' => 'Manual','created_at' => now(),'updated_at' => now()],
            ['name' => 'Automatic','created_at' => now(),'updated_at' => now()],
            ['name' => 'Semi-Automatic','created_at' => now(),'updated_at' => now()],
        ]);
    }
}