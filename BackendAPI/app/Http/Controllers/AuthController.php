<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    /**
     * Store a new user.
     *
     * @param  Request  $request
     * @return Response
     */
    public function register(Request $request)
    {
        //validate incoming request
        $validator = Validator::make($request->all(), [
            'name' => 'required|string',
            'email' => 'required|email|unique:users',
            'password' => 'required',
        ]);

        if ($validator->fails()) {
            $out = [
                "code" => 409,
                "message" => $validator->errors()->first(),
            ];
            return response()->json($out, $out['code']);
        } else {
            $user = new User();
            $user->name = $request->input('name');
            $user->email = $request->input('email');
            $plainPassword = $request->input('password');
            $user->password = app('hash')->make($plainPassword);
            $user->save();

            $out = [
                "code" => 201,
                "message" => "Register sukses",
                "result" => [
                    "user" => $user,
                ]
            ];
            return response()->json($out, $out['code']);
        }
    }

    public function login(Request $request)
    {
        //validate incoming request
        $validator = Validator::make($request->all(), [
            'email' => 'required|string',
            'password' => 'required|string',
        ]);

        if ($validator->fails()) {
            $out = [
                "code" => 409,
                "message" => $validator->errors()->first(),
            ];
            return response()->json($out, $out['code']);
        } else {
            $out = [
                "code" => 401,
                "message" => 'Unauthorized',
            ];

            $credentials = $request->only(['email', 'password']);
            if ($token = Auth::attempt($credentials)) {
                $user = Auth::user();
                $user['token'] = $token;
                $out = [
                    "code" => 200,
                    "result" => [
                        "user" => $user,
                    ]
                ];
            }
            return response()->json($out, $out['code']);
        }
    }
}
