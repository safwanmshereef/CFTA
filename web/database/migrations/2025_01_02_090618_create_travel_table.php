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
        Schema::create('travel', function (Blueprint $table) {
            $table->id();
            $table->string('user_id');
            $table->string('mot')->nullable();
            
            $table->string('vehicle_id')->nullable();
            $table->string('transport')->nullable();

            $table->string('from_lat')->nullable();
            $table->string('from_long')->nullable();

            $table->string('to_lat')->nullable();
            $table->string('to_long')->nullable();

            $table->string('start_date_time')->nullable();
            $table->string('end_date_time')->nullable();
            
            $table->string('usage_id')->nullable();
            
            $table->string('co2')->nullable();
            $table->string('media_id')->nullable();
            $table->string('status')->nullable();

            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('travel');
    }
};
