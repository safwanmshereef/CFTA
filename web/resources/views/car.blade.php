@extends('layout.app') 
@section('title') Home @endsection
@section('main')
   <div class="content-wrapper">
        <div class="row">
        <div class="col-lg-12 grid-margin stretch-card">
            <div class="card">
            <div class="card-body">
                <h4 class="card-title">User Car List</h4>
                <p class="card-description">
                List of all user added <code>car</code>
                </p>
                <div class="table-responsive pt-3">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th> # </th>
                            <th> User Name</th>
                            <th> Make </th>
                            <th> Model </th>
                            <th> Engine </th>
                            <th> Transmission </th>
                            <th> Fuel </th>
                            <th> Milage </th>
                            <th> CO2 </th>
                            <th> Status </th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($cars as $key =>  $car)
                            <tr>
                                <td>{{ $key + 1 }}</td>
                                <td>{{ $car->username }}</td>
                                <td>{{ $car->make }}</td>
                                <td>{{ $car->model }}</td>
                                <td>{{ $car->engine_size }}</td>
                                <td>{{ $car->transmission_type_name }}</td>
                                <td>{{ $car->fuel_type_name }}</td>
                                <td>{{ $car->milage }}</td>
                                <td>{{ $car->co2 }}</td>
                                <td>{{ $car->status_name }}</td>
                            </tr>
                        @endforeach
                    </tbody>
                </table>
                </div>
            </div>
            </div>
        </div>
        </div>
    </div>
@endsection      