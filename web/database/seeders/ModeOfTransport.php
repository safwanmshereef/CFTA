<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class ModeOfTransport extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        DB::table('modeoftranspotations')->insert([
            ['name' => 'Public','created_at' => now(),'updated_at' => now()],
            ['name' => 'Private','created_at' => now(),'updated_at' => now()],
            ['name' => 'Cycle','created_at' => now(),'updated_at' => now()],
            ['name' => 'Non EM','created_at' => now(),'updated_at' => now()],
        ]);
    }
}
