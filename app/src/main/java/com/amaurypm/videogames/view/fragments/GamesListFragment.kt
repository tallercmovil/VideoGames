package com.amaurypm.videogames.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amaurypm.videogames.R
import com.amaurypm.videogames.data.remote.GamesApi
import com.amaurypm.videogames.databinding.FragmentGamesListBinding
import com.amaurypm.videogames.databinding.GameElementBinding
import com.amaurypm.videogames.utils.Constants
import com.amaurypm.videogames.view.GameAdapter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GamesListFragment : Fragment() {

    private var _binding : FragmentGamesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGamesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        //Instanciamos nuestra interfaz a nuestra Api

        val gamesApi = retrofit.create(GamesApi::class.java)

        lifecycleScope.launch {

            try{
                //val games = gamesApi.getGames()
                val games = gamesApi.getGamesApiary()
                Log.d(Constants.LOGTAG, "Respuesta: $games")

                binding.rvGames.layoutManager = LinearLayoutManager(requireContext())
                //binding.rvGames.layoutManager = GridLayoutManager(requireContext(), 4)
                binding.rvGames.adapter = GameAdapter(games){ game ->
                    //Manejo del click
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, GameDetailFragment.newInstance(
                            game.id
                        ))
                        .addToBackStack(null)
                        .commit()
                }
            }
            catch (e: Exception){
                //Manejamos el error de conexión
            }
            finally {
                binding.pbLoading.visibility = View.INVISIBLE
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}