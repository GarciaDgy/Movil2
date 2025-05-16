namespace FCMDesktopSender
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            txtToken = new TextBox();
            txtTitulo = new TextBox();
            txtMensaje = new TextBox();
            btnEnviar = new Button();
            SuspendLayout();
            // 
            // txtToken
            // 
            txtToken.Location = new Point(263, 40);
            txtToken.Name = "txtToken";
            txtToken.Size = new Size(263, 31);
            txtToken.TabIndex = 0;
            // 
            // txtTitulo
            // 
            txtTitulo.Location = new Point(263, 140);
            txtTitulo.Name = "txtTitulo";
            txtTitulo.Size = new Size(263, 31);
            txtTitulo.TabIndex = 1;
            txtTitulo.TextChanged += txtTitulo_TextChanged;
            // 
            // txtMensaje
            // 
            txtMensaje.Location = new Point(138, 240);
            txtMensaje.Name = "txtMensaje";
            txtMensaje.Size = new Size(506, 31);
            txtMensaje.TabIndex = 2;
            // 
            // btnEnviar
            // 
            btnEnviar.Location = new Point(349, 348);
            btnEnviar.Name = "btnEnviar";
            btnEnviar.Size = new Size(112, 34);
            btnEnviar.TabIndex = 3;
            btnEnviar.Text = "Enviar";
            btnEnviar.UseVisualStyleBackColor = true;
            btnEnviar.Click += btnEnviar_Click;
            // 
            // Form1
            // 
            AutoScaleDimensions = new SizeF(10F, 25F);
            AutoScaleMode = AutoScaleMode.Font;
            ClientSize = new Size(800, 450);
            Controls.Add(btnEnviar);
            Controls.Add(txtMensaje);
            Controls.Add(txtTitulo);
            Controls.Add(txtToken);
            Name = "Form1";
            Text = "Form1";
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private TextBox txtToken;
        private TextBox txtTitulo;
        private TextBox txtMensaje;
        private Button btnEnviar;
    }
}
