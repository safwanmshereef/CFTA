<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class car extends Model
{
    protected $fillable = [
        'user_id', 'make', 'model', 'engine_size', 'transmission_type', 
        'fuel_type', 'status_id',
    ];

    public function user()
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    public function transmissionType()
    {
        return $this->belongsTo(transmission_type::class, 'transmission_type', 'tran_id');
    }

    public function fuelType()
    {
        return $this->belongsTo(fuel_type::class, 'fuel_type', 'fuel_id');
    }

    public function status()
    {
        return $this->belongsTo(status::class, 'status_id', 'status_id');
    }
}