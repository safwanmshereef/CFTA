<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('routes', function (Blueprint $table) {
            $table->id('route_id');
            $table->foreignId('user_id'); // Assuming 'users' table exists
            $table->string('from_latitude');
            $table->string('from_longitude');
            $table->string('to_latitude');
            $table->string('to_longitude');
            $table->string('start_time');
            $table->string('end_time');
            $table->string('total_duration'); // Duration in minutes or seconds
            $table->string('total_distance', 10, 2); // Distance in kilometers or miles
            $table->timestamps(); // created_at, updated_at
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('routes');
    }
};
