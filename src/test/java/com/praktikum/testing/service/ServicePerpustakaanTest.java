package com.praktikum.testing.service;

import com.praktikum.testing.model.Anggota;
import com.praktikum.testing.model.Buku;
import com.praktikum.testing.repository.RepositoryBuku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Service Perpustakaan")
public class ServicePerpustakaanTest {


    @Mock
    private RepositoryBuku mockRepositoryBuku;

    @Mock
    private KalkulatorDenda mockKalkulatorDenda;

    private ServicePerpustakaan servicePerpustakaan;
    private Buku bukuTest;
    private Anggota anggotaTest;

    @BeforeEach
    void setUp() {
        servicePerpustakaan = new ServicePerpustakaan(mockRepositoryBuku, mockKalkulatorDenda);
        bukuTest = new Buku("1234567890", "Pemrograman Java", "John Doe", 5, 150000.0);
        anggotaTest = new Anggota("A001", "John Student", "john@student.ac.id",
                "081234567890", Anggota.TipeAnggota.MAHASISWA);
    }
    @Test
    @DisplayName("Tambah buku berhasil ketika data valid dan buku belum ada")
    void testTambahBukuBerhasil() {
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.empty());
        when(mockRepositoryBuku.simpan(bukuTest)).thenReturn(true);
        boolean hasil = servicePerpustakaan.tambahBuku(bukuTest);

        assertTrue(hasil, "Harus berhasil menambah buku");
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
        verify(mockRepositoryBuku).simpan(bukuTest);
    }

    @Test
    @DisplayName("Tambah buku gagal ketika buku sudah ada")
    void testTambahBukuGagalBukuSudahAda() {
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        boolean hasil = servicePerpustakaan.tambahBuku(bukuTest);

        assertFalse(hasil, "Tidak boleh menambah buku yang sudah ada");
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
        verify(mockRepositoryBuku, never()).simpan(any(Buku.class));
    }

    @Test
    @DisplayName("Tambah buku gagal ketika data tidak valid")
    void testTambahBukuGagalDataTidakValid() {
        Buku bukuTidakValid = new Buku("123", "", "", 0, -100.0);

        boolean hasil = servicePerpustakaan.tambahBuku(bukuTidakValid);

        assertFalse(hasil, "Tidak boleh menambah buku dengan data tidak valid");
        verifyNoInteractions(mockRepositoryBuku);
    }

    @Test
    @DisplayName("Cari buku by ISBN berhasil")
    void testCariBukuByIsbnBerhasil() {
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        Optional<Buku> hasil = servicePerpustakaan.cariBukuByIsbn("1234567890");

        assertTrue(hasil.isPresent(), "Harus menemukan buku");
        assertEquals("Pemrograman Java", hasil.get().getJudul());
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
    }

    @Test
    @DisplayName("Cari buku by judul berhasil")
    void testCariBukuByJudul() {
        List<Buku> daftarBuku = Arrays.asList(bukuTest);
        when(mockRepositoryBuku.cariByJudul("Java")).thenReturn(daftarBuku);

        List<Buku> hasil = servicePerpustakaan.cariBukuByJudul("Java");

        assertEquals(1, hasil.size());
        assertEquals("Pemrograman Java", hasil.get(0).getJudul());
        verify(mockRepositoryBuku).cariByJudul("Java");
    }

    @Test
    @DisplayName("Buku tersedia")
    void testBukuTersedia() {
        bukuTest.setJumlahTersedia(3);
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        assertTrue(servicePerpustakaan.bukuTersedia("1234567890"));

        bukuTest.setJumlahTersedia(0);
        assertFalse(servicePerpustakaan.bukuTersedia("1234567890"));
    }

    @Test
    @DisplayName("Get jumlah tersedia")
    void testGetJumlahTersedia() {
        bukuTest.setJumlahTersedia(3);
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        int jumlah = servicePerpustakaan.getJumlahTersedia("1234567890");

        assertEquals(3, jumlah);
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
    }

    @Test
    @DisplayName("Get jumlah tersedia untuk buku yang tidak ada")
    void testGetJumlahTersediaBukuTidakAda() {
        when(mockRepositoryBuku.cariByIsbn("9999999999")).thenReturn(Optional.empty());

        int jumlah = servicePerpustakaan.getJumlahTersedia("9999999999");

        assertEquals(0, jumlah);
        verify(mockRepositoryBuku).cariByIsbn("9999999999");
    }

    @Test
    @DisplayName("Hitung rating rata-rata buku")
    void testHitungRatingRataRata() {
        // Test akan gagal karena method belum ada
        double rating = servicePerpustakaan.hitungRatingRataRata("1234567890");
        assertEquals(4.5, rating, 0.1); }
}