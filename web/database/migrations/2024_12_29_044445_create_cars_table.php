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
        Schema::create('cars', function (Blueprint $table) {
            $table->id('car_id');
            $table->foreignId('user_id');
            $table->string('make')->nullable();
            $table->string('model')->nullable();
            $table->string('engine_size')->nullable();
            $table->string('transmission_type')->nullable();
            $table->string('fuel_type')->nullable();
            $table->string('milage')->nullable();
            $table->string('co2')->nullable();
            $table->string('prediction')->nullable();
            $table->string('status_id')->nullable();
            $table->string('emition')->nullable();
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
        Schema::dropIfExists('cars');
    }
};
