# Cek Obat 355

Aplikasi Android checklist belajar obat untuk siswa SMK Farmasi. Berisi 355
nama obat generik beserta indikasinya, dikelompokkan per kategori. Ketuk obat
untuk menandainya sudah dipelajari — status centang tersimpan permanen di HP.

## Fitur v1

- Daftar 355 obat dikelompokkan per kategori, tiap item tampil nama + indikasi
- Ketuk item untuk centang / hapus centang (tersimpan di DataStore Preferences)
- Filter: Semua / Sudah dicentang / Belum dicentang
- Pencarian nama obat (substring, abaikan kapital)
- Header progres "X / 355 sudah dipelajari" + progress bar
- Tombol reset (hapus semua centang) dengan dialog konfirmasi

## Data

Sumber data: `~/workspace/materi-sekolah/build_daftar_obat_355.py`
(disalin ke `app/src/main/assets/drugs.json`). Total terverifikasi: **355 obat**.

Catatan: data sumber berisi **20 kategori**. PDF "Daftar 355 Obat" yang
beredar memakai pengelompokan lain (14 kategori, isi sebagian berbeda).
Aplikasi ini mengikuti data sumber (script Python), bukan PDF.

## Build

Gradle tidak bisa jalan di VM dev (network Java diblokir sandbox), jadi build
resmi lewat GitHub Actions (`.github/workflows/android.yml`): JDK 17,
Gradle 8.2, AGP 8.2.2. Setiap push ke `main` menjalankan unit test lalu
build debug APK.

## Teknologi

Kotlin native + Jetpack Compose, single-module, single-activity.
Package: `com.rezz.obatcheck`. Min SDK 26, target SDK 34.
