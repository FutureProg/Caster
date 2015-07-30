<?php include_once "../../phpreq/start_session.php"; ?>
<!--Start Content Here-->
<div id="content-container" style="margin:10px;margin-left:0;top:90px;height:80%;">    
                    <div style="background-color:white;margin: 20px 20px;border: 1px solid black;">
                        <div style="text-align:center;padding: 30px 0">
                            <img src="images/title.png" style="width:25%;"/> 
                            <h1>Welcome to Caster!</h1>
                            Caster is a website dedicated to helping people share their stories with the world. <br/>
                            Create a podcast, upload it, share it. <br/> Cast your voice. 
                        </div>                                                                            
                        <hr/>
                        <div>
                            <?php if(isset($_SESSION['user_id'])){ ?>
                            <h2 style="text-align:center">Subscriptions</h2>
                            <div id="subscriptions">
                            </div>
                            <?php }?>                            
                            <h2 style="text-align:center">Recent Uploads</h2>           
                            <div id="recent-uploads"></div>
                        </div>
                    </div>                    
                            <script src="js/index.js"></script>
                    <?php include '../../phpreq/footer.php'; ?>
                </div>
<script type='text/javascript'>
            document.title = "Caster";
        </script>
</div>
<!--End Content Here------>
