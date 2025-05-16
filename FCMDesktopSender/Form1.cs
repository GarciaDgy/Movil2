using FirebaseAdmin;
using FirebaseAdmin.Messaging;
using Google.Apis.Auth.OAuth2;
using System;
using System.Windows.Forms;

namespace FCMDesktopSender
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            FirebaseApp.Create(new AppOptions()
            {
                Credential = GoogleCredential.FromFile(@"C:\Users\Yoselin Duran Garcia\source\repos\FCMDesktopSender\FCMDesktopSender\bin\Debug\service-account.json"),
            });
        }



        private async void btnEnviar_Click(object sender, EventArgs e)
        {
            string token = txtToken.Text;
            string titulo = txtTitulo.Text;
            string cuerpo = txtMensaje.Text;

            var mensaje = new FirebaseAdmin.Messaging.Message()
            {
                Token = token,
                Data = new Dictionary<string, string>()
        {
            { "title", titulo },
            { "body", cuerpo }
        },
                Notification = new Notification
                {
                    Title = titulo,
                    Body = cuerpo
                }
            };

            try
            {
                string respuesta = await FirebaseMessaging.DefaultInstance.SendAsync(mensaje);
                MessageBox.Show("Enviado con éxito: " + respuesta);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error al enviar: " + ex.Message);
            }
        }


        private void txtTitulo_TextChanged(object sender, EventArgs e)
        {

        }
    }
}