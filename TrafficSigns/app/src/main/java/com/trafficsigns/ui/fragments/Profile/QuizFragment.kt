package com.trafficsigns.ui.fragments.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.trafficsigns.R
import com.trafficsigns.data.MyProfileViewModel
import com.trafficsigns.data.TrafficSign
import com.trafficsigns.databinding.FragmentQuizBinding
import com.trafficsigns.ui.constants.ToastMessage
import java.util.*
import kotlin.collections.ArrayList

const val QUIZ_TAG = "QuizFragment"
class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var mProfileViewModel: MyProfileViewModel
    private var gainedScore = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
                .inflate( inflater, R.layout.fragment_quiz, container, false )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mProfileViewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        mProfileViewModel.myProfile.observeOnce(viewLifecycleOwner, {
            if (it.knownTrafficSigns?.count()!! < 5) {
                Toast.makeText(requireContext(), ToastMessage.QUIZ_WARNING, Toast.LENGTH_LONG).show()
            }
            else {
                binding.start.isEnabled = true
            }
        })

        binding.start.setOnClickListener {
            startTest()
        }
        binding.doneButtond.setOnClickListener {
            updateScores()
            binding.root.findNavController().navigate(R.id.action_quizFragment_to_mainScreenFragment)
        }
    }

    private fun startTest() {
        quizVisible()

        mProfileViewModel.myProfile.observeOnce(viewLifecycleOwner, {
            val signs = it.knownTrafficSigns?.shuffled()
            var answers = arrayListOf<String>()
            var i = 0
            loadQuizLayout(answers, signs, i)

                binding.nextButton.setOnClickListener {
                    checkCorrectAnswer(signs?.get(i)?.name.toString())
                    i+=1
                    if (i < 5){
                        answers = ArrayList()
                        loadQuizLayout(answers, signs, i)
                    }
                    else {
                     quizInvisible()
                    }
                }
        })
    }
    private fun quizVisible() {
        binding.start.isEnabled = false
        binding.start.visibility = View.INVISIBLE
        binding.quiz.visibility = View.VISIBLE
        binding.nextButton.visibility = View.VISIBLE
        binding.nextButton.isEnabled = true
    }

    private fun quizInvisible() {
        binding.nextButton.visibility = View.INVISIBLE
        binding.nextButton.isEnabled = false
        binding.quiz.visibility = View.INVISIBLE
        binding.description.text = "Your score is: $gainedScore /5 \n Keep learning!"
        binding.doneButtond.isEnabled = true
        binding.doneButtond.visibility = View.VISIBLE
    }

    private fun getFalseAnswer(answers: ArrayList<String>, signs: List<TrafficSign>?): String {
        var rand = Random().nextInt(signs?.count() as Int)
        while(answers.contains(signs[rand].name)){
            rand = Random().nextInt(signs.count())
        }
        return signs[rand].name.toString()
    }

    private fun loadQuizLayout(answers: ArrayList<String>, signs: List<TrafficSign>?, index: Int) {
        Glide.with(binding.root).load(signs?.get(index)?.image)
                .override(binding.quizImage.width,binding.quizImage.height)
                .into(binding.quizImage)

        answers.add(signs?.get(index)?.name.toString())
        answers.add(getFalseAnswer(answers,signs))
        answers.add(getFalseAnswer(answers,signs))
        answers.shuffle()

        binding.radioButton.text = answers[0]
        binding.radioButton2.text = answers[1]
        binding.radioButton3.text = answers[2]
    }

    private fun updateScores() {
        mProfileViewModel.myProfile.observeOnce(viewLifecycleOwner, {
            it?.scores?.add(gainedScore)
            mProfileViewModel.updateProfile(it)
        })
    }

    private fun checkCorrectAnswer(correctAnswer: String) {
        val id = binding.radioGroup.checkedRadioButtonId
        val value = ( view?.findViewById<RadioButton>(id))?.text.toString();
        Log.d(QUIZ_TAG, value)
        if (value == correctAnswer){
            gainedScore++
        }
        binding.radioGroup.clearCheck()
    }

}