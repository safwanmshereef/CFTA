<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Route extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id',
        'from_latitude',
        'from_longitude',
        'to_latitude',
        'to_longitude',
        'start_time',
        'end_time',
        'total_duration',
        'total_distance',
    ];
}
