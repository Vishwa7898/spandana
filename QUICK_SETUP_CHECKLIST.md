# 🚀 Quick Setup Checklist

## ✅ Pre-Implementation Checklist

Before you start, make sure:
- [ ] Android Studio is open
- [ ] Project is synced
- [ ] Backup your current code (commit to git)

---

## 📋 Implementation Order (Follow this exactly!)

### Phase 1: Core Resources (15 minutes)

1. **Colors** 
   - [ ] Create `res/values/colors.xml`
   - [ ] Create `res/values-night/colors.xml`
   - [ ] Copy all color definitions

2. **Dimensions**
   - [ ] Create `res/values/dimens.xml`
   - [ ] Add all dimension values

3. **Strings**
   - [ ] Update `res/values/strings.xml`
   - [ ] Add all new string resources

---

### Phase 2: Drawables (20 minutes)

**Shape Drawables:**
- [ ] `circular_progress.xml`
- [ ] `gradient_overlay.xml`
- [ ] `profile_gradient_overlay.xml`
- [ ] `dot_green.xml`
- [ ] `day_selector_selected.xml`
- [ ] `day_selector_unselected.xml`
- [ ] `bottom_nav_color.xml`
- [ ] `switch_thumb_color.xml`
- [ ] `switch_track_color.xml`

**Icon Drawables:**
- [ ] `ic_theme.xml`
- [ ] `ic_sun.xml`
- [ ] `ic_moon.xml`
- [ ] `ic_arrow_forward.xml`
- [ ] `ic_ai.xml`
- [ ] `ic_notifications.xml`
- [ ] `ic_help.xml`
- [ ] `ic_info.xml`
- [ ] `ic_back.xml`
- [ ] `ic_clock.xml`
- [ ] `ic_star.xml`
- [ ] `ic_calendar.xml`

**Tip:** Right-click `res/drawable` → New → Drawable Resource File

---

### Phase 3: Themes (10 minutes)

1. **Update themes.xml**
   - [ ] Replace entire `res/values/themes.xml`
   - [ ] Add all custom styles
   - [ ] Add button styles
   - [ ] Add text styles

2. **Create values-night folder**
   - [ ] Right-click `res/values` → New → Values Resource File
   - [ ] Name: `themes`, Qualifiers: Night Mode → Night
   - [ ] Add dark theme variations

---

### Phase 4: Layouts (25 minutes)

1. **Main Activity**
   - [ ] Update `activity_main.xml`
   - [ ] Add ConstraintLayout structure
   - [ ] Add Fragment container
   - [ ] Add Bottom Navigation

2. **Bottom Navigation Menu**
   - [ ] Create `res/menu/bottom_nav_menu.xml`
   - [ ] Add all navigation items

3. **Home Fragment**
   - [ ] Create `fragment_home.xml`
   - [ ] Add CoordinatorLayout
   - [ ] Add AppBar with theme toggle
   - [ ] Add week days container
   - [ ] Add progress card
   - [ ] Add daily goal card
   - [ ] Add AI recommendation

4. **Profile Fragment**
   - [ ] Create `fragment_profile.xml`
   - [ ] Add CollapsingToolbarLayout
   - [ ] Add background image
   - [ ] Add stats cards
   - [ ] Add settings items

5. **Day Selector Item**
   - [ ] Create `item_day_selector.xml`
   - [ ] Add day text and number

---

### Phase 5: Code (30 minutes)

1. **Theme Manager**
   - [ ] Create `utils/ThemeManager.kt`
   - [ ] Copy complete class
   - [ ] Verify package name

2. **Update MainActivity**
   - [ ] Add ThemeManager initialization
   - [ ] Update bottom navigation setup
   - [ ] Import all required classes

3. **Update HomeFragment**
   - [ ] Add theme toggle functionality
   - [ ] Add week days setup
   - [ ] Add progress calculation
   - [ ] Add click listeners

4. **Update ProfileFragment**
   - [ ] Add dark mode switch
   - [ ] Add stats setup
   - [ ] Add settings click listeners
   - [ ] Add logout dialog

---

### Phase 6: Dependencies (5 minutes)

- [ ] Update `app/build.gradle.kts`
- [ ] Add Material Design dependency
- [ ] Add Gson dependency
- [ ] Add Glide dependency
- [ ] Sync Gradle
- [ ] Wait for build to complete

---

### Phase 7: Testing (15 minutes)

**Light Mode Tests:**
- [ ] App launches successfully
- [ ] Home screen displays correctly
- [ ] Theme toggle button visible
- [ ] Bottom navigation works
- [ ] Navigate to Profile
- [ ] Navigate to Categories
- [ ] Navigate to Trends

**Dark Mode Tests:**
- [ ] Toggle to dark mode
- [ ] All screens adapt to dark theme
- [ ] Colors look correct
- [ ] Toggle back to light mode
- [ ] App restarts in correct theme

**Profile Page Tests:**
- [ ] Background image displays
- [ ] Stats cards visible
- [ ] Dark mode switch works
- [ ] Settings items clickable
- [ ] Logout button works

---

## 🐛 Quick Troubleshooting

### Build Errors:

**Error: Unresolved reference**
```
Solution: Sync Gradle → Build → Clean Project → Rebuild
```

**Error: Cannot find symbol R.drawable.xxx**
```
Solution: Check drawable file name matches exactly
```

**Error: Theme not found**
```
Solution: Verify themes.xml has no syntax errors
```

### Runtime Issues:

**App crashes on launch:**
```kotlin
// Check MainActivity onCreate:
themeManager = ThemeManager.getInstance(this)
themeManager.init() // Must be before super.onCreate()
super.onCreate(savedInstanceState)
```

**Theme toggle doesn't work:**
```kotlin
// Verify ThemeManager instance is created correctly
// Check SharedPreferences permissions
```

**Colors not changing:**
```
Solution: 
1. Check values-night folder exists
2. Verify color names match exactly
3. Clean & Rebuild project
```

---

## 📊 Progress Tracker

Track your implementation:

```
Phase 1: Colors & Resources    [___] 0%
Phase 2: Drawables             [___] 0%
Phase 3: Themes                [___] 0%
Phase 4: Layouts               [___] 0%
Phase 5: Code                  [___] 0%
Phase 6: Dependencies          [___] 0%
Phase 7: Testing               [___] 0%

Overall Progress: ____%
```

---

## 🎉 Completion

When all checkboxes are ✅:

1. **Test thoroughly** on both light and dark modes
2. **Commit your changes** to version control
3. **Document any customizations** you made
4. **Share with your team** or deploy

---

## 💪 You're Ready!

මේ checklist එක follow කරලා step-by-step implement කරන්න. ඕනෑම අවස්ථාවක issue එකක් ආවොත් troubleshooting section එක බලන්න.

Good luck! 🚀
