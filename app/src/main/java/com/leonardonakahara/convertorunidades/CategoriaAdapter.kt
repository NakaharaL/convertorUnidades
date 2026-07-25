package com.leonardonakahara.convertorunidades

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

data class CategoriaUnidade(
    val id: String,
    val nome: String,
    val iconeResId: Int
)

class CategoriaAdapter(
    private val context: Context,
    private val categorias: List<CategoriaUnidade>
) : BaseAdapter() {

    private var posicaoSelecionada = 0

    override fun getCount(): Int = categorias.size

    override fun getItem(position: Int): Any = categorias[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_categoria, parent, false)

        val imgCategoria = view.findViewById<ImageView>(R.id.imgCategoria)
        val tvNomeCategoria = view.findViewById<TextView>(R.id.tvNomeCategoria)

        val item = categorias[position]

        tvNomeCategoria.text = item.nome
        imgCategoria.setImageResource(item.iconeResId)

        if (position == posicaoSelecionada) {
            view.alpha = 1.0f
        } else {
            view.alpha = 0.5f
        }

        return view
    }

    fun setItemSelecionado(position: Int) {
        posicaoSelecionada = position
        notifyDataSetChanged()
    }

    @Suppress("unused")
    fun getCategoriaSelecionada(): CategoriaUnidade = categorias[posicaoSelecionada]
}