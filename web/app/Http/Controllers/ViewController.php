<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Models\transmission_type;
use App\Models\fuel_type;
use App\Models\car;
use App\Models\travel;
use App\Models\travelusage;
use App\Models\modeoftranspotation;


class ViewController extends Controller
{
    public function login(){return view('login');}
    public function home(){return view('home');}

    public function transmissiontype(){
        $data = transmission_type::all();
        return view('transmissiontype',compact('data'));
    }
    public function fueltype(){
        $data = fuel_type::all();
        return view('fueltype',compact('data'));
    }

    public function car(){
        $cars = DB::table('cars as c')
        ->leftJoin('transmission_types as tt', 'c.transmission_type', '=', 'tt.tran_id')
        ->leftJoin('fuel_types as ft', 'c.fuel_type', '=', 'ft.fuel_id')
        ->leftJoin('statuses as s', 'c.status_id', '=', 's.status_id')
        ->leftJoin('users as u', 'c.user_id', '=', 'u.id')
        ->select(
            'c.car_id as car_id',
            'c.user_id',
            'u.name as username',
            'c.make',
            'c.model',
            'c.engine_size',
            'c.milage',
            'c.co2',
            'c.transmission_type',
            'tt.name as transmission_type_name',
            'c.fuel_type',
            'ft.name as fuel_type_name',
            'c.status_id',
            's.name as status_name',
            'c.created_at',
            'c.updated_at'
        )
        ->get();
        return view('car',compact('cars'));
    }

    public function createfueltype(){return view('addnewfueltype');}
    public function createtransmissiontype(){return view('addnewtransmissiontype');}

    
    public function travel(){
        $data = travel::all();
        return view('travel',compact('data'));
    }
    public function modeoftranspotations(){
        $data = modeoftranspotation::all();
        return view('modeoftranspotations',compact('data'));
    }
    public function createmodeoftranspotations (){return view('addnewmodeoftranspotations');}
}
