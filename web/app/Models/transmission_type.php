<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class transmission_type extends Model
{
    use HasFactory;
    protected $fillable = ['name'];
    public function cars()
    {
        return $this->hasMany(Car::class, 'transmission_type', 'tran_id');
    }
}
