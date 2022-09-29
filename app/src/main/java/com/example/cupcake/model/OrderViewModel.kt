/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Harga untuk satu cupcake */
private const val PRICE_PER_CUPCAKE = 2.00

/** Biaya tambahan untuk pengambilan pesanan di hari yang sama */
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

/**
 * [OrderViewModel] menyimpan informasi tentang pesanan cupcake dalam hal kuantitas, rasa, dan
 * tanggal pengambilan. Ia juga tahu bagaimana menghitung harga total berdasarkan rincian pesanan ini.
 */
class OrderViewModel : ViewModel() {

    // jumlah cupcake dalam pesanan
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    // Rasa Cupcake dalam pesanan
    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    // kemungkinan tanggal konsumsi dalam pesanan
    val dateOptions: List<String> = getPickupOptions()

    // Pickup date
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    // Harga dalam pesanan sejauh ini
    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price) {
        // Format harga ke dalam mata uang lokal dan kembalikan ini sebagai LiveData<String>
        NumberFormat.getCurrencyInstance().format(it)
    }

    init {
        // atur nilai awal pesanan
        resetOrder()
    }

    /**
     * mengatur jumlah cupcake dalam pesanan.
     *
     * @param numberCupcakes to order
     */
    fun setQuantity(numberCupcakes: Int) {
        _quantity.value = numberCupcakes
        updatePrice()
    }

    /**
     * mengatur rasa cupcake dalam pesanan ini. hanya 1 rasa yang dapat di pilh dalam sekian pesanan.
     *
     * @param desiredFlavor is the cupcake flavor as a string
     */
    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }

    /**
     * mengatur tanggal jemput pesanan.
     *
     * @param pickupDate is the date for pickup as a string
     */
    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        updatePrice()
    }

    /**
     * Mengembalikan nilai true jika rasa belum dipilih untuk pesanan..
     */
    fun hasNoFlavorSet(): Boolean {
        return _flavor.value.isNullOrEmpty()
    }

    /**
     * Atur ulang pesanan dengan menggunakan nilai default awal untuk kuantitas, rasa, tanggal, dan harga.
     */
    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0
    }

    /**
     * Memperbarui harga berdasarkan detail pesanan.
     */
    private fun updatePrice() {
        var calculatedPrice = (quantity.value ?: 0) * PRICE_PER_CUPCAKE
        // Jika pengguna memilih opsi pertama (hari ini) untuk pengambilan, tambahkan biaya tambahan
        if (dateOptions[0] == _date.value) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }

    /**
     * Mengembalikan daftar opsi tanggal yang dimulai dengan tanggal saat ini dan 3 tanggal berikut.
     */
    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        repeat(4) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }
}