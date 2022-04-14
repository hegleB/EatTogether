package com.qure.eattogether2.view.post

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.CreatePostCategoryAdapter
import com.qure.eattogether2.databinding.FragmentCreatePostCategoryBinding


class CreatePostCategoryFragment : Fragment() {

    private lateinit var binding : FragmentCreatePostCategoryBinding
    private lateinit var categoryList : Array<String>
    private val args by navArgs<CreatePostCategoryFragmentArgs>()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_post_category,
            container,
            false
        )


        setCreateCategoryMenu()
        getCategoryData()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    fun setCreateCategoryMenu(){

        binding.apply {

            createCategoryToolbar.setNavigationIcon(R.drawable.ic_back)
            createCategoryToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    fun getCategoryData() {

        categoryList = resources.getStringArray(R.array.categey_name)
        val adapter = CreatePostCategoryAdapter(categoryList)
        binding.createCategoryRecyclerview.adapter = adapter


        adapter.setItemClickListener(object : CreatePostCategoryAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                val directions =
                    CreatePostCategoryFragmentDirections.actionCreatePostCategoryFragmentToCreatePostFragment(
                        categoryList[position], args.title, args.content)
                findNavController().navigate(directions)
            }

        })



    }

    override fun onPause() {
        super.onPause()
        System.out.println("category onPause")
    }

    override fun onStop() {
        super.onStop()
        System.out.println("category onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        System.out.println("category onDestoryView")
    }

    override fun onDestroy() {
        super.onDestroy()
        System.out.println("category onDestory")
    }


}