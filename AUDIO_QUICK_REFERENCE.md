# Audio Narration Quick Reference

## 📋 What You Need

### Accounts (All Free Tier)
1. **Cloudflare Account** - for R2 storage
   - Sign up: https://dash.cloudflare.com/sign-up
   
2. **11Labs Account** - for text-to-speech
   - Sign up: https://elevenlabs.io/
   
3. **Firebase Service Account Key** - you already have this project

---

## 🚀 Quick Start (30 Minutes Total)

### Step 1: Cloudflare R2 (15 min)
```
1. Create account at dash.cloudflare.com
2. Go to R2 → Create bucket → name: "faunadex-audio"
3. Enable public access → get public URL (https://pub-xxxxx.r2.dev)
4. Create API token → save Access Key ID, Secret Key, Endpoint
```

### Step 2: 11Labs (5 min)
```
1. Sign up at elevenlabs.io
2. Get API key from Profile → API keys
3. That's it!
```

### Step 3: Test Setup (5 min)
```powershell
# Install dependencies
pip install requests boto3 firebase-admin

# Edit test_audio_setup.py with your keys
notepad test_audio_setup.py

# Run test
python test_audio_setup.py
```

### Step 4: Generate All Audio (5 min)
```powershell
# Edit audio_generator.py with your keys
notepad audio_generator.py

# Run generator
python audio_generator.py
```

### Step 5: Test in App (2 min)
```
1. Sync Gradle in Android Studio
2. Run app
3. Open animal detail
4. Tap play button!
```

---

## 🔑 Keys You Need

Create a file `keys.txt` and save these:

```
ELEVENLABS_API_KEY=
R2_ACCESS_KEY_ID=
R2_SECRET_ACCESS_KEY=
R2_ENDPOINT=
R2_BUCKET_NAME=faunadex-audio
R2_PUBLIC_URL=
```

---

## 🎯 How It Works

```
Animal Description (Firestore)
         ↓
   11Labs API (generates MP3)
         ↓
   Cloudflare R2 (stores MP3)
         ↓
   Public URL (https://pub-xxxxx.r2.dev/audio/komodo.mp3)
         ↓
   Firestore (saves URL in animal.audio_description_url)
         ↓
   Android App (streams with ExoPlayer)
```

---

## 💰 Costs

| Service | Free Tier | Cost |
|---------|-----------|------|
| Cloudflare R2 | 10 GB storage + unlimited egress | **$0** |
| 11Labs | 10,000 chars/month (~50 animals) | **$0** |
| Firebase | Existing usage | **$0** |
| **Total** | | **$0/month** |

---

## 🎵 Features Implemented

✅ Play/pause audio narration
✅ Progress bar with time display
✅ Beautiful waveform animation while playing
✅ Loading states
✅ Error handling
✅ Single audio plays at a time
✅ Stops when navigating away
✅ Works with all education levels (SD/SMP/SMA)

---

## 📱 User Experience

1. User opens animal detail page
2. Sees play button or audio player bar
3. Taps play → audio streams instantly
4. Sees progress bar and waveform animation
5. Can pause/resume/stop anytime
6. Audio stops when leaving the page

---

## 🔧 File Changes Made

```
Modified:
✓ Animal.kt - added audioDescriptionUrl, audioFunFactUrl
✓ build.gradle.kts - added Media3 ExoPlayer
✓ AnimalDetailViewModel.kt - added audio player manager
✓ AnimalDetailScreen.kt - integrated audio UI

Created:
✓ AudioPlayerManager.kt - audio playback logic
✓ AudioPlayerBar.kt - audio player UI component
✓ audio_generator.py - audio generation script
✓ test_audio_setup.py - test script
✓ AUDIO_SETUP_GUIDE.md - full guide
```

---

## 🐛 Troubleshooting

**Audio not playing?**
- Check internet connection
- Verify audio URL exists in Firestore
- Check Android logs

**Script fails?**
- Run test_audio_setup.py first
- Verify all keys are correct
- Check you have Python installed

**11Labs API error?**
- Check API key is valid
- Verify you have credits left (check elevenlabs.io)

---

## 📞 Support Checklist

If something doesn't work, check:
- [ ] Cloudflare R2 bucket created
- [ ] Public access enabled on bucket
- [ ] R2 API tokens created correctly
- [ ] 11Labs account has credits
- [ ] Firebase service account key downloaded
- [ ] All keys filled in scripts
- [ ] Python dependencies installed
- [ ] Gradle synced in Android Studio

---

## 🎓 Architecture Decision Rationale

**Why pre-generated audio?**
- Cost-effective: generate once, use forever
- Fast playback: no waiting for generation
- Consistent quality
- Works offline (with caching)
- No API key exposure in app

**Why Cloudflare R2?**
- FREE bandwidth (unlike AWS S3)
- 10 GB free storage
- Fast global CDN
- S3-compatible API

**Why 11Labs?**
- Best quality text-to-speech
- Supports Indonesian language
- Natural-sounding voices
- Good free tier

---

That's it! Follow the AUDIO_SETUP_GUIDE.md for detailed instructions.
