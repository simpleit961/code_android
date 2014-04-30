
1. Create .gitignore to ignore file or folder that we don't want to commit

2. git configure credential.helper cache 
	to remember password of git

3.  git configure credential.helper exit
	to disable cache password

4. git rm -r --cache .gen/* 
	remove tag file that we add to git before but want to ignore it in .gitignore file

5. git add --force [file name]
	force add file in case we need to add some file in black list (.gitignore)
6. To make application full screen (put int MainActivity
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);	

	
7. Different between 

        android:showAsAction="never"  --> not show in action
        android:showAsAction="always" -->  show in action



  <item
        android:id="@+id/action_settings"
        android:orderInCategory="100"
        android:showAsAction="never"
        android:title="@string/action_settings"/>
    <item
        android:id="@+id/menu_load"
        android:icon="@drawable/ic_action_refresh"
        android:orderInCategory="200"
        android:showAsAction="always"
        android:title="Load"/>

8. to enable home button in action bar 
	actionBar.setHomeButtonEnabled(true);

9. git checkout -- <file name> 
	to discard change from working directory

10. ncurse tool for git
	tig

11. Convert image ( another proccess possible)
	conver image.jpeg new_image.png
	!!!: install imagemagick 
12. Good tutorial menu for android 
	http://www.tutorialsbuzz.com/2013/06/android-option-menu-example.html
13. Add new icon to android project 
	 File → New → Other... → Android → Android Icon

14. Shot cut in eclipse
	Ctrl + .  --> go to next error
	Ctrl + ,  --> go to previous error
15. Add remote
	git remote --> show remote
	git add remote <remote_name> <link github.git>
	git push <remote_name> <branch_name> 

16. Tab view android
	http://www.androidhive.info/2013/10/android-tab-layout-with-swipeable-views-1/	
