@extends('layout.app') 
@section('title') User Travel @endsection
@section('main')
    <div class="content-wrapper">
        <div class="row">
            <div class="col-lg-12 grid-margin stretch-card">
                <div class="card">
                <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <h4 class="card-title">User Travel</h4>
                    <a href="{{ route('create.fueltype.get') }}">
                        <button type="button" class="btn btn-primary btn-icon-text">
                            <i class="ti-plus btn-icon-prepend"></i>
                            Add New
                        </button>
                    </a>
                </div>
                    <p class="card-description">
                    List of all <code>user travel</code>
                    </p>
                    <div class="table-responsive pt-3">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th> # </th>
                                <th> User Name </th>
                                <th> MOT </th>
                                <th> Vehicle </th>
                                <th> From Location </th>
                                <th> To Location </th>
                                <th> Start Date And Time </th>
                                <th> End Date And Time </th>
                                <th> Usage  </th>
                                <th> CO2 </th>
                                <th> Media Name </th>
                            </tr>
                        </thead>
                        <tbody>
                           @foreach($data as $key => $datas)
                                <tr>
                                    <td>{{ $key + 1 }}</td>
                                    <td>{{ $datas->name }}</td>
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