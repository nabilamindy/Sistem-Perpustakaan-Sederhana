package com.praktikum.testing.repository;

import com.praktikum.testing.model.Buku;
import com.praktikum.testing.repository.MockRepositoryBuku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class untuk MockRepositoryBuku
 *
 * TUJUAN PEMBELAJARAN:
 * - Memahami cara menggunakan mock manual
 * - Melihat bagaimana mock bekerja secara internal
 * - Membandingkan behavior mock manual dengan real implementation
 */
@DisplayName("Test Mock Repository Buku - Manual Mock Implementation")
class MockRepositoryBukuTest {

    private MockRepositoryBuku mockRepository;
    private Buku buku1;
    private Buku buku2;
    private Buku buku3;

    @BeforeEach
    void setup() {
        // Reset mock repository sebelum setiap test
        mockRepository = new MockRepositoryBuku();

        // Setup sample data
        buku1 = new Buku("1234567890", "Pemrograman Java", "John Doe", 5, 180000.0);
        buku2 = new Buku("0987654321", "Algoritma dan Struktur Data", "Jane Smith", 3, 120000.0);
        buku3 = new Buku("11111111111", "Java Advanced", "John Doe", 4, 180000.0);
    }

    @Test
    @DisplayName("Simpan buku baru - harus berhasil")
    void testSimpanBukuBaru() {
        boolean hasil = mockRepository.simpan(buku1);

        assertTrue(hasil, "Harus berhasil menyimpan buku baru");
        assertEquals(1, mockRepository.ukuran(), "Repository harus berisi 1 buku");
        assertTrue(mockRepository.mengandung("1234567890"), "Repository harus mengandung ISBN buku!");
    }

    @Test
    @DisplayName("Simpan buku null - harus gagal")
    void testSimpanBukuNull() {
        boolean hasil = mockRepository.simpan(null);

        assertFalse(hasil, "Harus gagal menyimpan buku null");
        assertEquals(0, mockRepository.ukuran(), "Repository harus tetap kosong");
    }

    @Test
    @DisplayName("Simpan buku dengan ISBN null - harus gagal")
    void testSimpanBukuDenganIsbnNull() {
        Buku bukuInvalid = new Buku();
        bukuInvalid.setJudul("Judul Tanpa ISBN");

        boolean hasil = mockRepository.simpan(bukuInvalid);

        assertFalse(hasil, "Harus gagal menyimpan buku tanpa ISBN");
        assertEquals(0, mockRepository.ukuran(), "Repository harus tetap kosong");
    }

    @Test
    @DisplayName("Simpan buku duplikat - harus overwrite")
    void testSimpanBukuDuplikat() {
        mockRepository.simpan(buku1);
        Buku bukuUpdated = new Buku("1234567890", "Java Programming Updated", "John Doe Updated", 10, 200000.0);

        boolean hasil = mockRepository.simpan(bukuUpdated);

        assertTrue(hasil, "Harus berhasil menyimpan (overwrite)");
        assertEquals(1, mockRepository.ukuran(), "Repository harus tetap berisi 1 buku");

        Optional<Buku> bukuDiRepository = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuDiRepository.isPresent());
        assertEquals("Java Programming Updated", bukuDiRepository.get().getJudul());
    }

    @Test
    @DisplayName("Cari buku by ISBN - buku ditemukan")
    void testCariByIsbnBukuDitemukan() {
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);

        Optional<Buku> hasil = mockRepository.cariByIsbn("0987654321");

        assertTrue(hasil.isPresent(), "Harus menemukan buku2");
        assertEquals("Algoritma dan Struktur Data", hasil.get().getJudul());
        assertEquals("Jane Smith", hasil.get().getPengarang());
    }

    @Test
    @DisplayName("Cari buku by ISBN - buku tidak ditemukan")
    void testCariByIsbnBukuTidakDitemukan() {
        mockRepository.simpan(buku1);

        Optional<Buku> hasil = mockRepository.cariByIsbn("9999999999");

        assertFalse(hasil.isPresent(), "Harus mengembalikan Optional.empty()");
    }

    @Test
    @DisplayName("Cari buku by ISBN null - harus empty")
    void testCariByIsbnNull() {
        Optional<Buku> hasil = mockRepository.cariByIsbn(null);

        assertFalse(hasil.isPresent(), "Harus mengembalikan Optional.empty() untuk ISBN null");
    }

    @Test
    @DisplayName("Cari buku by judul - case insensitive")
    void testCariByJudulCaseInsensitive() {
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);
        mockRepository.simpan(buku3);

        List<Buku> hasil1 = mockRepository.cariByJudul("java");
        List<Buku> hasil2 = mockRepository.cariByJudul("JAVA");
        List<Buku> hasil3 = mockRepository.cariByJudul("Java");

        assertEquals(2, hasil1.size(), "Harus menemukan 2 buku dengan kata java");
        assertEquals(2, hasil2.size(), "Harus case insensitive");
        assertEquals(2, hasil3.size(), "Harus case insensitive");

        assertTrue(hasil1.stream().anyMatch(b -> b.getJudul().equals("Pemrograman Java")));
        assertTrue(hasil1.stream().anyMatch(b -> b.getJudul().equals("Java Advanced")));
    }

    @Test
    @DisplayName("Cari buku by judul partial match")
    void testCariByJudulPartialMatch() {
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);
        mockRepository.simpan(buku3);

        List<Buku> hasil = mockRepository.cariByJudul("Algoritma");

        assertEquals(1, hasil.size());
        assertEquals("Algoritma dan Struktur Data", hasil.get(0).getJudul());
    }

    @Test
    @DisplayName("Cari buku by judul kosong - harus empty list")
    void testCariByJudulKosong() {
        mockRepository.simpan(buku1);

        List<Buku> hasil1 = mockRepository.cariByJudul("");
        List<Buku> hasil2 = mockRepository.cariByJudul(" ");
        List<Buku> hasil3 = mockRepository.cariByJudul(null);

        assertTrue(hasil1.isEmpty(), "Harus empty untuk string kosong");
        assertTrue(hasil2.isEmpty(), "Harus empty untuk whitespace");
        assertTrue(hasil3.isEmpty(), "Harus empty untuk null");
    }

    @Test
    @DisplayName("Cari buku by pengarang")
    void testCariByPengarang() {
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);
        mockRepository.simpan(buku3);

        List<Buku> hasil = mockRepository.cariByPengarang("John Doe");

        assertEquals(2, hasil.size(), "Harus menemukan 2 buku oleh John Doe");
        assertTrue(hasil.stream().allMatch(b -> b.getPengarang().equals("John Doe")));
    }

    @Test
    @DisplayName("Hapus buku yang ada - harus berhasil")
    void testHapusBukuYangAda() {
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);
        assertEquals(2, mockRepository.ukuran());

        boolean hasil = mockRepository.hapus("1234567890");

        assertTrue(hasil, "Harus berhasil menghapus");
        assertEquals(1, mockRepository.ukuran(), "Harus tersisa 1 buku");
        assertFalse(mockRepository.mengandung("1234567890"), "Buku1 harus sudah dihapus");
        assertTrue(mockRepository.mengandung("0987654321"), "Buku2 harus masih ada");
    }

    @Test
    @DisplayName("Hapus buku yang tidak ada - harus gagal")
    void testHapusBukuYangTidakAda() {
        mockRepository.simpan(buku1);

        boolean hasil = mockRepository.hapus("9999999999");

        assertFalse(hasil, "Harus gagal menghapus buku yang tidak ada");
        assertEquals(1, mockRepository.ukuran(), "Repository harus tetap berisi 1 buku");
    }

    @Test
    @DisplayName("Hapus dengan ISBN null - harus gagal")
    void testHapusDenganIsbnNull() {
        boolean hasil = mockRepository.hapus(null);

        assertFalse(hasil, "Harus gagal untuk ISBN null");
    }

    @Test
    @DisplayName("Update jumlah tersedia - valid update")
    void testUpdateJumlahTersediaValid() {
        mockRepository.simpan(buku1);

        boolean hasil = mockRepository.updateJumlahTersedia("1234567890", 3);

        assertTrue(hasil, "Harus berhasil update");

        Optional<Buku> bukuUpdated = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuUpdated.isPresent());
        assertEquals(3, bukuUpdated.get().getJumlahTersedia(), "Jumlah tersedia harus terupdate");
        assertEquals(5, bukuUpdated.get().getJumlahTotal(), "Jumlah total harus tetap sama");
    }

    @Test
    @DisplayName("Update jumlah tersedia - melebihi jumlah total harus gagal")
    void testUpdateJumlahTersediaMelebihiTotal() {
        mockRepository.simpan(buku1);

        boolean hasil = mockRepository.updateJumlahTersedia("1234567890", 10);

        assertFalse(hasil, "Harus gagal update karena melebihi jumlah total");

        Optional<Buku> buku = mockRepository.cariByIsbn("1234567890");
        assertTrue(buku.isPresent());
        assertEquals(5, buku.get().getJumlahTersedia(), "Jumlah tersedia harus tetap sama");
    }

    @Test
    @DisplayName("Update jumlah tersedia - jumlah negatif harus gagal")
    void testUpdateJumlahTersediaNegatif() {
        mockRepository.simpan(buku1);

        boolean hasil = mockRepository.updateJumlahTersedia("1234567890", -1);

        assertFalse(hasil, "Harus gagal untuk jumlah negatif");
    }

    @Test
    @DisplayName("Update jumlah tersedia - buku tidak ditemukan harus gagal")
    void testUpdateJumlahTersediaBukuTidakDitemukan() {
        boolean hasil = mockRepository.updateJumlahTersedia("9999999999", 5);

        assertFalse(hasil, "Harus gagal untuk buku yang tidak ada");
    }

    @Test
    @DisplayName("Cari semua buku - repository kosong")
    void testCariSemuaRepositoryKosong() {
        List<Buku> hasil = mockRepository.cariSemua();

        assertTrue(hasil.isEmpty(), "Harus empty list untuk repository kosong");
    }

    @Test
    @DisplayName("Cari semua buku - repository berisi beberapa buku")
    void testCariSemuaRepositoryBerisi() {
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);
        mockRepository.simpan(buku3);

        List<Buku> hasil = mockRepository.cariSemua();

        assertEquals(3, hasil.size(), "Harus mengembalikan semua buku");
        assertTrue(hasil.contains(buku1));
        assertTrue(hasil.contains(buku2));
        assertTrue(hasil.contains(buku3));
    }

    @Test
    @DisplayName("Bersihkan repository - harus kosong")
    void testBersihkanRepository() {
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);
        assertEquals(2, mockRepository.ukuran());

        mockRepository.bersihkan();

        assertEquals(0, mockRepository.ukuran(), "Repository harus kosong setelah dibersihkan");
        assertTrue(mockRepository.cariSemua().isEmpty());
    }

    @Test
    @DisplayName("Integration test - simulasi flow peminjaman buku")
    void testIntegrationFlowPeminjaman() {
        mockRepository.simpan(buku1);
        assertEquals(1, mockRepository.ukuran());

        Optional<Buku> bukuAwal = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuAwal.isPresent());
        assertEquals(5, bukuAwal.get().getJumlahTersedia());

        boolean updateBerhasil = mockRepository.updateJumlahTersedia("1234567890", 4);
        assertTrue(updateBerhasil);

        Optional<Buku> bukuSetelahPinjam = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuSetelahPinjam.isPresent());
        assertEquals(4, bukuSetelahPinjam.get().getJumlahTersedia());

        updateBerhasil = mockRepository.updateJumlahTersedia("1234567890", 5);
        assertTrue(updateBerhasil);

        Optional<Buku> bukuSetelahKembali = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuSetelahKembali.isPresent());
        assertEquals(5, bukuSetelahKembali.get().getJumlahTersedia());
    }

    @Test
    @DisplayName("Test utility methods - ukuran dan mengandung")
    void testUtilityMethods() {
        assertEquals(0, mockRepository.ukuran());
        assertFalse(mockRepository.mengandung("1234567890"));

        mockRepository.simpan(buku1);
        assertEquals(1, mockRepository.ukuran());
        assertTrue(mockRepository.mengandung("1234567890"));
        assertFalse(mockRepository.mengandung("999999999"));

        mockRepository.simpan(buku2);
        assertEquals(2, mockRepository.ukuran());
        assertTrue(mockRepository.mengandung("0987654321"));
    }
}
