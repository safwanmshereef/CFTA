<?php
namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class FuelTypeSeeder extends Seeder
{
    public function run()
    {
        DB::table('fuel_types')->insert([
            ['name' => 'Petrol','created_at' => now(),'updated_at' => now()],
            ['name' => 'Diesel','created_at' => now(),'updated_at' => now()],
            ['name' => 'Electric','created_at' => now(),'updated_at' => now()],
            ['name' => 'Hybrid','created_at' => now(),'updated_at' => now()],
        ]);
    }
}