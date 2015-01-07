package com.satersoft.stardestroyer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.Plus;
import com.satersoft.example.games.basegameutils.BaseGameUtils;
import com.satersoft.stardestroyer.domain.InfoWrapper;
import com.satersoft.stardestroyer.ui.FullScreenActivity;


public class MainActivity extends FullScreenActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // Client used to interact with Google APIs
    private GoogleApiClient googleAPIClient;

    public static final int GAME_RESULT = 96969;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // request codes we use when invoking an external activity
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "SD";

    private InfoWrapper info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        info = (InfoWrapper) getIntent().getExtras().get("info");
        if(info.score >= 0) {
            // check score!
            Log.e("SCORED", "Score: " + info.score);
            onEnteredScore(info.score);
            info.score = -1;
        }

        // Set our view.
        setContentView(R.layout.activity_main);

        // Create the Google API Client with access to Plus and Games
        googleAPIClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        // create fragments
        /*mMainMenuFragment = new MainMenuFragment();
        mGameplayFragment = new GameplayFragment();
        mWinFragment = new WinFragment();*/

        // listen to fragment events
        /*mMainMenuFragment.setListener(this);
        mGameplayFragment.setListener(this);
        mWinFragment.setListener(this);*/

        // add initial fragment (welcome fragment)
        /*getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mMainMenuFragment).commit();*/

        // IMPORTANT: if this Activity supported rotation, we'd have to be
        // more careful about adding the fragment, since the fragment would
        // already be there after rotation and trying to add it again would
        // result in overlapping fragments. But since we don't support rotation,
        // we don't deal with that for code simplicity.

        // load outbox from file
        //mOutbox.loadLocal(this);

    }

    private boolean isSignedIn() {
        return (googleAPIClient != null && googleAPIClient.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart(): connecting");
        /*if(checkGPServices()) {
            if (!googleAPIClient.isConnecting() &&
                    !googleAPIClient.isConnected()) {
                googleAPIClient.connect();
            }
        }*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): disconnecting");
        if (googleAPIClient.isConnected()) {
            googleAPIClient.disconnect();
        }
    }

    public void onStartGameRequested(boolean hardMode) {
        startGame(hardMode);
    }

    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(googleAPIClient),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    public void onShowLeaderboardsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(googleAPIClient),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(checkGPServices()) {
            if (!googleAPIClient.isConnecting() &&
                    !googleAPIClient.isConnected()) {
                googleAPIClient.connect();
            }
        }*/

    }

    @Override
    public void onRestoreInstanceState(Bundle saved) {
        info = (InfoWrapper) getIntent().getExtras().get("info");
        Log.e("RESTORRREEE", " x");
        if(info.score >= 0) {
            // check score!
            Log.e("SCORED", "Score: " + info.score);
            onEnteredScore(info.score);
            info.score = -1;
        }
    }

    private boolean checkGPServices() {
        int gpService = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        boolean fine = false;
        switch(gpService) {
            case ConnectionResult.SUCCESS:
                // Connect GP services, sign in, hide button
                fine = true;
                break;
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                GooglePlayServicesUtil.getErrorDialog(gpService, this, 1).show();
                break;
        }
        return fine;
    }

    public void playGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("info", info);
        startActivity(intent);
        finish();
    }

    public void openSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void signGoogle(View v) {
        if(checkGPServices()) {
            if (!googleAPIClient.isConnecting() &&
                    !googleAPIClient.isConnected()) {
                googleAPIClient.connect();
                setShowSignIn(false);
            }
        }
    }

    /**
     * Start gameplay. This means updating some status variables and switching
     * to the "gameplay" screen (the screen where the user types the score they want).
     *
     * @param hardMode whether to start gameplay in "hard mode".
     */
    void startGame(boolean hardMode) {
        //mHardMode = hardMode;
        //switchToFragment(mGameplayFragment);
    }

    public void onEnteredScore(int requestedScore) {
        checkForAchievements(requestedScore, requestedScore);

        // update leaderboards
        updateLeaderboards(requestedScore);

        // push those accomplishments to the cloud, if signed in
        pushAccomplishments();
    }

    /**
     * Check for achievements and unlock the appropriate ones.
     *
     * @param requestedScore the score the user requested.
     * @param finalScore the score the user got.
     */
    void checkForAchievements(int requestedScore, int finalScore) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        if (finalScore > 10) {
            mOutbox.mLowAchievement = true;
            achievementToast(getString(R.string.achievement_prime_toast_text));
        }
        if (requestedScore == 9999) {
            mOutbox.mArrogantAchievement = true;
            achievementToast(getString(R.string.achievement_arrogant_toast_text));
        }
        if (requestedScore == 0) {
            mOutbox.mHumbleAchievement = true;
            achievementToast(getString(R.string.achievement_humble_toast_text));
        }
        if (finalScore == 1337) {
            mOutbox.mLeetAchievement = true;
            achievementToast(getString(R.string.achievement_leet_toast_text));
        }
        if(mOutbox.mBoredSteps > 5) {
            mOutbox.mBoredAchievement = true;
            achievementToast(getString(R.string.achievement_leet_toast_text));
        }
        mOutbox.mBoredSteps++;
    }

    void unlockAchievement(int achievementId, String fallbackString) {
        if (isSignedIn()) {
            Games.Achievements.unlock(googleAPIClient, getString(achievementId));
        } else {
            Toast.makeText(this, getString(R.string.achievement) + ": " + fallbackString,
                    Toast.LENGTH_LONG).show();
        }
    }

    void achievementToast(String achievement) {
        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!isSignedIn()) {
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement,
                    Toast.LENGTH_LONG).show();
        }
    }


    void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            mOutbox.saveLocal(this);
            return;
        }
        if (mOutbox.mLowAchievement) {
            Games.Achievements.unlock(googleAPIClient, getString(R.string.achievement_low));
            mOutbox.mLowAchievement = false;
        }
        if (mOutbox.mArrogantAchievement) {
            Games.Achievements.unlock(googleAPIClient, getString(R.string.achievement_arrogant));
            mOutbox.mArrogantAchievement = false;
        }
        if (mOutbox.mHumbleAchievement) {
            Games.Achievements.unlock(googleAPIClient, getString(R.string.achievement_humble));
            mOutbox.mHumbleAchievement = false;
        }
        if (mOutbox.mLeetAchievement) {
            Games.Achievements.unlock(googleAPIClient, getString(R.string.achievement_leet));
            mOutbox.mLeetAchievement = false;
        }
        if (mOutbox.mBoredSteps > 0) {
            Games.Achievements.increment(googleAPIClient, getString(R.string.achievement_really_bored),
                    mOutbox.mBoredSteps);
            Games.Achievements.increment(googleAPIClient, getString(R.string.achievement_bored),
                    mOutbox.mBoredSteps);
        }
        if (mOutbox.score >= 0) {
            Games.Leaderboards.submitScore(googleAPIClient, getString(R.string.leaderboard_score),
                    mOutbox.score);
            mOutbox.score = -1;
        }
        mOutbox.saveLocal(this);
    }

    /**
     * Update leaderboards with the user's score.
     *
     * @param finalScore The score the user got.
     */
    void updateLeaderboards(int finalScore) {
        mOutbox.score = finalScore;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!googleAPIClient.isConnecting() &&
                        !googleAPIClient.isConnected()) {
                    googleAPIClient.connect();
                }

            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
            }
        } else if(requestCode == GAME_RESULT) {
            if (resultCode == RESULT_OK) {
                info = (InfoWrapper) intent.getExtras().get("info");
                if(info.score >= 0) {
                    // check score!
                    Log.e("SCORED", "Score: " + info.score);
                    info.score = -1;
                }
                // TODO: achievements!
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        // Show sign-out button on main menu
        //mMainMenuFragment.setShowSignInButton(false);

        // Show "you are signed in" message on win screen, with no sign in button.
        //mWinFragment.setShowSignInButton(false);

        // Set the greeting appropriately on main menu
        Player p = Games.Players.getCurrentPlayer(googleAPIClient);
        String displayName;
        if (p == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
        //mMainMenuFragment.setGreeting("Hello, " + displayName);


        // if we have accomplishments to push, push them
        if (!mOutbox.isEmpty()) {
            pushAccomplishments();
            Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): attempting to connect");
        if (!googleAPIClient.isConnecting() &&
                !googleAPIClient.isConnected()) {
            googleAPIClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed(): already resolving");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
            if (!BaseGameUtils.resolveConnectionFailure(this, googleAPIClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        // Sign-in failed, so show sign-in button on main menu
        /*mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
        mMainMenuFragment.setShowSignInButton(true);
        mWinFragment.setShowSignInButton(true);*/
    }

    public void onSignInButtonClicked() {
        // start the sign-in flow
        mSignInClicked = true;
        // Make sure the app is not already connected or attempting to connect
        if (!googleAPIClient.isConnecting() &&
                !googleAPIClient.isConnected()) {
            googleAPIClient.connect();
        }

    }

    public void onSignOutButtonClicked() {
        mSignInClicked = false;
        Games.signOut(googleAPIClient);
        if (googleAPIClient.isConnected()) {
            googleAPIClient.disconnect();
        }

        /*mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
        mMainMenuFragment.setShowSignInButton(true);
        mWinFragment.setShowSignInButton(true);*/
    }

    public void updateUI() {
        findViewById(R.id.btnSign).setVisibility(
                showSignIn() ? View.VISIBLE : View.GONE);
        findViewById(R.id.signed_in_bar).setVisibility(
                showSignIn() ? View.GONE : View.VISIBLE);
    }

    class AccomplishmentsOutbox {
        boolean mLowAchievement = false;
        boolean mHumbleAchievement = false;
        boolean mLeetAchievement = false;
        boolean mArrogantAchievement = false;
        int mBoredSteps = 0;
        boolean mBoredAchievement = false;
        int score;

        boolean isEmpty() {
            return !mLowAchievement && !mHumbleAchievement && !mLeetAchievement &&
                    !mArrogantAchievement && mBoredSteps == 0 && !mBoredAchievement;
        }

        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }
    }
}
