<?php
include_once 'phpreq/start_session.php';
$profile_id = filter_input(INPUT_GET,"user");
$myprofile =  (isset($_SESSION['user_id']) && $profile_id == $_SESSION['user_id']);
?>
<div id="content-container" style="margin:10px;margin-left:0;top:90px;height:80%;">
    <div id="profile-page">
        <div id="profile-left-bar">
            <label id="profile-image-container">
                <img id="profile-image">
                <img id="profile-image-camera" src="/images/edit_camera.png"/>
                <input id="my-profile-image-uploader" name="new_profile_image" accept="image/png, image/jpg, image/jpeg" type="file" style="display:none;"/>
                </img>
            </label>
        <div id="profile-user-name" class="circle-header">
            <h1></h1>
        </div>
        <div id="profile-subscribe-button">
            <img class="profile-subscribe-button-image" src="/images/subscribe_button.png"/>
        </div>
        <p id="profile-description">
            <!-- Description: Lorem ipsum dolor sit amet,
duis et eu vitae, diam optio donec at dolor, eleifend eu volutpat                                   suspendisse, <a href="">nulla magna</a> elit. -->
        </p>
        <br/>
        <a href="settings.php" id="settings-link">settings</a>
    </div>
    <div id="profile-content">
        <!--<nav id="profile-nav-bar">
<a href="" id="profile-activity-item" style="border-left: 1px solid;">Activity</a>
<a href="" id="profile-podcasts-item">Podcasts</a>
<a href="" id="profile-playlists-item">Playlists</a>
</nav>-->
        <div id="profile-activity-area">
        </div>
    </div>
</div>
<?php include '../../phpreq/footer.php'; ?>
<script type='text/javascript'>
    document.title = "Caster - Profile";
</script>
<script src="/js/profile.js"></script> 
</div>
