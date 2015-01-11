package com.pauphilet_romero.destagram;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.FriendsAdapter;
import com.pauphilet_romero.destagram.adapters.MainTabsPagerAdapter;
import com.pauphilet_romero.destagram.models.Friend;
import com.pauphilet_romero.destagram.models.Media;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Activité gérant les onglets "home - ajouter un média - amis"
 */
public class MainTabsActivity extends FragmentActivity implements ActionBar.TabListener {
    // Storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";

    // Required for camera operations in order to save the image file on resume.
    private String mCurrentPhotoPath = null;
    private String mCurrentPhotoFullSizePath = null;

    public String getmCurrentPhotoFullSizePath() {
        return mCurrentPhotoFullSizePath;
    }

    public void setmCurrentPhotoFullSizePath(String mCurrentPhotoFullSizePath) {
        this.mCurrentPhotoFullSizePath = mCurrentPhotoFullSizePath;
    }
    public static String getCapturedPhotoPathKey() {
        return CAPTURED_PHOTO_PATH_KEY;
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    private ViewPager viewPager;
    private MainTabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    private FriendsAdapter adapter;

    ///// éléments pour le slidingPane /////
    // le layout
    MySlidingPaneLayout mSlidingPanel;
    // la liste du menu
    ListView mMenuList;
    // l'icône dans l'action bar
    ImageView appImage;
    // le contenu de la liste du menu
    String [] menuTitles = new String[]{"Mes médias", "Déconnexion"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);

        // changement d'icone dans la barre d'action
        getActionBar().setIcon(R.drawable.ic_menu);

        // Initialisation pour les onglets
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new MainTabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Ajout des onglets
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_new_picture).setTabListener(this), 0, false);
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_menu_home).setTabListener(this), 1, true);
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_group).setTabListener(this), 2, false);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);

                // changement de titre du fragment quand on change d'onglet
                if(position == 0) {
                    getActionBar().setTitle(getString(R.string.title_activity_upload));
                } else if(position == 1) {
                    getActionBar().setTitle(getString(R.string.title_activity_profil));
                } else if(position == 2) {
                    getActionBar().setTitle(getString(R.string.title_activity_friends_list));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        // Gestion du menu avec le slidingPane
        mSlidingPanel = (MySlidingPaneLayout) findViewById(R.id.SlidingPanel);
        mMenuList = (ListView) findViewById(R.id.MenuList);
        appImage = (ImageView)findViewById(android.R.id.home);

        // Listener sur le menu afin de lancer l'action adéquat selon l'élément du menu cliqué
        mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // redirige vers l'activité listant les médias de l'utilisateur
                if (position == 0) {
                    Intent oldIntent = getIntent();
                    String token = oldIntent.getStringExtra("token");
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                // déconnecte l'utilisateur de l'application
                } else if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    // on vide le "back stack" pour empêcher l'utilisation du bouton précédent
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        mMenuList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, menuTitles));

        mSlidingPanel.setPanelSlideListener(panelListener);

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }

    MySlidingPaneLayout.PanelSlideListener panelListener = new MySlidingPaneLayout.PanelSlideListener(){

        @Override
        public void onPanelClosed(View arg0) {
            // TODO Auto-genxxerated method stub        getActionBar().setTitle(getString(R.string.app_name));
            appImage.animate().rotation(0);
        }

        @Override
        public void onPanelOpened(View arg0) {
            // TODO Auto-generated method stub
            appImage.animate().rotation(90);
        }

        @Override
        public void onPanelSlide(View arg0, float arg1) {
            // TODO Auto-generated method stub

        }

    };

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mSlidingPanel.isOpen()){
                    appImage.animate().rotation(0);
                    mSlidingPanel.closePane();
                }
                else{
                    appImage.animate().rotation(90);
                    mSlidingPanel.openPane();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ajout d'un ami
     * @param view
     */
    public void addFriend(View view)
    {
        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), R.string.error_empty_friend, Toast.LENGTH_SHORT);
        final Intent intent = getIntent();
        final String token = intent.getStringExtra("token");
        EditText usernameField = (EditText) findViewById(R.id.newFriendField);
        final String username= usernameField.getText().toString();

        if(username!= null && !username.equals("") ) {
            // on vérifie la connexion Internet
            ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
            if(connection.isConnectingToInternet()) {
                // nouveau thread pour la requête HTTP
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // Requête http
                        HttpRequest request = null;
                        try {
                            request = new HttpRequest("http://destagram.zz.mu/add_friend.php?token="+ URLEncoder.encode(token, "UTF-8")+"&username="+ URLEncoder.encode(username, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        try {
                            // on traduit la réponse en objet JSON
                            JSONObject json = new JSONObject(request.getResponse());

                            error = json.getBoolean("error");
                            // si il n'y a pas d'erreur
                            if (!error) {
                                JSONObject jsonFriend = json.getJSONObject("friend");
                                final Friend friend = new Friend(jsonFriend);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        adapter.add(friend);
                                    }
                                });
                            } else if(json.getInt("code")==1){
                                toast.setText(R.string.error_not_existing_account);
                                toast.show();
                            }
                            else if(json.getInt("code")==2) {
                                toast.setText(R.string.error_friend_in_list);
                                toast.show();
                            }
                            else if(json.getInt("code")==3) {
                                toast.setText(R.string.error_add_myself);
                                toast.show();
                            }
                            else
                            {
                                toast.setText(R.string.error_connect);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } else {
                toast.setText(R.string.error_internet);
                toast.show();
            }
        }
        else {
            toast.show();
        }

    }
}
