<?php
namespace App\Http\Controllers;

use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use Symfony\Component\HttpFoundation\StreamedResponse;
use App\Models\car;

class DataExportController extends Controller
{
    public function exportToCsv()
    {
        try {
            // Fetch data from the database
            // $data = DB::table('cars as c')
            // ->leftJoin('transmission_types as tt', 'c.transmission_type', '=', 'tt.tran_id')
            // ->leftJoin('fuel_types as ft', 'c.fuel_type', '=', 'ft.fuel_id')
            // ->leftJoin('statuses as s', 'c.status_id', '=', 's.status_id')
            // ->leftJoin('users as u', 'c.user_id', '=', 'u.id')
            // ->select(
            //     'c.user_id',
            //     'u.name as username',
            //     'c.make',
            //     'c.model',
            //     'c.engine_size',
            //     'c.milage',
            //     'c.co2',
            //     'c.transmission_type',
            //     'tt.name as transmission_type_name',
            //     'c.fuel_type',
            //     'ft.name as fuel_type_name'
            // )
            // ->get();
            $data = DB::table('travel as t')
            ->leftJoin('users as u', 't.user_id', '=', 'u.id')
            ->leftJoin('cars as c', 't.vehicle_id', '=', 'c.car_id')
            ->leftJoin('travelusages as tu', 't.usage_id', '=', 'tu.id')
            ->leftJoin('transmission_types as tt', 'c.transmission_type', '=', 'tt.tran_id')
            ->leftJoin('fuel_types as ty', 'c.fuel_type', '=', 'ty.fuel_id')
            ->select(
                't.id',
                't.user_id',
                'u.name as user_name',
                't.mot',
                't.vehicle_id',
                'c.make',
                'c.model',
                'c.engine_size',
                'c.transmission_type',
                'tt.name as transmission_type_name',
                'c.fuel_type',
                'ty.name as fuel_type_name',
                'c.milage',
                'c.co2',
                'c.emition',
                'c.prediction',
                't.transport',
                't.from_lat',
                't.from_long',
                't.to_lat',
                't.to_long',
                't.start_date_time',
                't.end_date_time',
                't.usage_id',
                'tu.gprs',
                'tu.sms',
                'tu.phone',
                't.media_id',
                't.status',
                't.created_at',
                't.updated_at'
            )
            ->whereNull('t.co2')
            ->get();
        

            if ($data->isEmpty()) {
                return response()->json(['message' => 'No data available to export'], 404);
            }

            // Convert data to JSON
            $jsonData = json_encode($data, JSON_PRETTY_PRINT);

            // Define a fixed file name and path
            $fileName = 'data_export.json';
            $filePath = 'public/exports/' . $fileName;

            // Store the JSON file in the storage folder (overwrites if it already exists)
            Storage::put($filePath, $jsonData);

            // Get the file URL
            $fileUrl = Storage::url($filePath);

            // Return the file URL in the response
            return response()->json([
                'message' => 'Data exported successfully',
                'file_url' => 'http://192.168.82.20:8080'.$fileUrl
            ], 200);
        } catch (\Exception $e) {
            return response()->json(['message' => 'An error occurred: ' . $e->getMessage()], 500);
        }
    }
}
