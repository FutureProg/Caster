<?php
include 'phpreq/start_session.php';
?>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="author" content="Nickolas Morrison">
        <meta name="description" content="Cast your voice">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <link href="style.css" rel="stylesheet"/>
        <title>Caster - Upload</title>
    </head>
    <body>
        <div id="body-container">
            <?php include 'phpreq/topbar.php';?>
            <div id="main-content">
                <div id="content-container" style="margin:10px;margin-left:0;top:90px;height:80%;">
                    <div id="upload-container">
                        <h1 style="text-align:center">Upload</h1>
                        <div style="display:inline">
                            <div style="display:inline-text">
                                <h2>Cover image</h2>
                                <span style="position: relative;">
                                    <img id="podcast-upload-image-preview" src=""></img>
                                <span style="position:absolute;margin-left:2px;width:100%;">
                                    <label>400x400px recommended</label>
                                    <input type="file" id="image-uploader" class="button-class-1" name="file[]" accept="image/png, image/jpg, image/jpeg" type="file"></input><br/><br/><br/>
                                <span>
                                    <input type="checkbox" id="download-checkbox" name="dc"/>
                                    <label for="download-checkbox"> Allow Downloads</label><br/><br/>
                                </span>
                                <select id="share-settings" title="Global - Share with the world. We'll make an rss feed for you.
                                                                   Local - Share with only the people on Caster. This gives you access to things like stats (not yet available)&#013;
                                                                   Private - Only you can see this podcast">
                                    <option value="GLOBAL">Global</option>
                                    <option value="LOCAL">Local</option>
                                    <option value="PRIVATE">Private</option>
                                </select>
                                <label for="share-settings">Sharing Settings</label>
                                <!--<button style="width:30px;height:30px;padding:0;border-radius: 50px;margin:5px"
                                        class="button-class-2 tooltip"
                                        title="oh boy">
                                    ?
                                </button>-->
                                </span>
                            </span>
                        <br/>
                        <h2>Audio File</h2>
                        <input type="file" id="podcast-uploader" class="button-class-1" name="file[]" accept="audio/x-mpeg"></input>
                    <h2>Title</h2>
                    <input type="text" id="upload-title-area"></input>
                <br/>
                <h2>Description</h2>
                <textarea id="upload-description-area"></textarea>
                <h2>Category</h2>
                <select id="category" title="Select a category">
                	<option value="ACTIVISM">Activism</option>
                	<option value="COMEDY">Comedy</option>
                	<option value="EDUCATION">Education</option>
                	<option value="ENTERTAINMENT">Entertainment</option>
                	<option value="FILM">Film</option>
                	<option value="GAMING">Gaming</option>
                	<option value="HOWTO">How To</option>
                	<option value="MUSIC">Music</option>
                	<option value="NEWS">News</option>
                	<option value="PEOPLE">People</option>
                	<option value="SCIENCE">Science</option>
                	<option value="SPORTS">Sports</option>
                	<option value="TECHNOLOGY">Technology</option>
                	<option value="TRAVEL">Travel</option>
                	<option value="VEHICLES">Vehicles</option>
                </select>
                <h2>Tags</h2>
                <p>Seperated by spaces</p>
                <input type="text" id="upload-tags-area"></input>
        </div>
        </div>
    <br/>
    <button class="button-class-2" onclick="submit()">SUBMIT</button>
    </div>
<?php include 'phpreq/footer.php' ?>
</div>
</div>
</div>
<script src="js/main.js"></script>
<script src="js/upload.js"></script>
</body>
</html>
