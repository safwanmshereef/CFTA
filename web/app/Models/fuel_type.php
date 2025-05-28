<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class fuel_type extends Model
{
    use HasFactory;
    protected $fillable = ['name'];
    public function cars()
    {
        return $this->hasMany(Car::class, 'fuel_type', 'fuel_id');
    }
}
