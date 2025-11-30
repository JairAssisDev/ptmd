import { useState, useEffect } from 'react'
import {
  Typography,
  Button,
  Box,
  Grid,
  TextField,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
} from '@mui/material'
import {
  Add as AddIcon,
  CloudUpload as CloudUploadIcon,
  CheckCircle as CheckCircleIcon,
} from '@mui/icons-material'
import { useDropzone } from 'react-dropzone'
import { format } from 'date-fns'
import {
  consultationService,
  ConsultationResponse,
  ConsultationRequest,
  ConfirmDiagnosisRequest,
} from '../services/consultationService'

const MedicoDashboard = () => {
  const [consultations, setConsultations] = useState<ConsultationResponse[]>([])
  const [error, setError] = useState('')
  const [newConsultationOpen, setNewConsultationOpen] = useState(false)
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false)
  const [selectedConsultation, setSelectedConsultation] = useState<ConsultationResponse | null>(null)
  const [formData, setFormData] = useState({
    patientNome: '',
    patientSexo: 'MASCULINO' as 'MASCULINO' | 'FEMININO' | 'OUTRO',
    patientDataNascimento: '',
    image: null as File | null,
  })
  const [confirmDiagnosis, setConfirmDiagnosis] = useState('')
  const [uploading, setUploading] = useState(false)

  useEffect(() => {
    loadConsultations()
  }, [])

  const loadConsultations = async () => {
    try {
      const data = await consultationService.getMyConsultations()
      setConsultations(data)
    } catch (err: any) {
      setError('Erro ao carregar consultas')
    }
  }

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    accept: {
      'image/*': ['.png', '.jpg', '.jpeg'],
    },
    onDrop: (acceptedFiles) => {
      if (acceptedFiles.length > 0) {
        setFormData({ ...formData, image: acceptedFiles[0] })
      }
    },
    maxFiles: 1,
  })

  const handleCreateConsultation = async () => {
    if (!formData.image) {
      setError('Por favor, selecione uma imagem')
      return
    }

    setUploading(true)
    setError('')

    try {
      const request: ConsultationRequest = {
        patient: {
          nome: formData.patientNome,
          sexo: formData.patientSexo,
          dataNascimento: formData.patientDataNascimento || undefined,
        },
        image: formData.image,
      }

      const newConsultation = await consultationService.createConsultation(request)
      setConsultations([newConsultation, ...consultations])
      setNewConsultationOpen(false)
      setFormData({
        patientNome: '',
        patientSexo: 'MASCULINO',
        patientDataNascimento: '',
        image: null,
      })
    } catch (err: any) {
      setError(err.response?.data?.error || 'Erro ao criar consulta')
    } finally {
      setUploading(false)
    }
  }

  const handleOpenConfirmDialog = (consultation: ConsultationResponse) => {
    setSelectedConsultation(consultation)
    setConfirmDiagnosis(consultation.aiDiagnosis || '')
    setConfirmDialogOpen(true)
  }

  const handleConfirmDiagnosis = async () => {
    if (!selectedConsultation) return

    try {
      const request: ConfirmDiagnosisRequest = {
        finalDiagnosis: confirmDiagnosis,
      }

      const updated = await consultationService.confirmDiagnosis(
        selectedConsultation.id,
        request
      )

      setConsultations(
        consultations.map((c) => (c.id === updated.id ? updated : c))
      )
      setConfirmDialogOpen(false)
      setSelectedConsultation(null)
      setConfirmDiagnosis('')
    } catch (err: any) {
      setError(err.response?.data?.error || 'Erro ao confirmar diagnóstico')
    }
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Minhas Consultas</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setNewConsultationOpen(true)}
        >
          Nova Consulta
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Paciente</TableCell>
              <TableCell>Data</TableCell>
              <TableCell>Diagnóstico IA</TableCell>
              <TableCell>Confiança</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {consultations.map((consultation) => (
              <TableRow key={consultation.id}>
                <TableCell>{consultation.patient.nome}</TableCell>
                <TableCell>
                  {format(new Date(consultation.createdAt), 'dd/MM/yyyy HH:mm')}
                </TableCell>
                <TableCell>
                  {consultation.aiDiagnosis || 'N/A'}
                  {consultation.multClass && (
                    <Chip
                      label={consultation.multClass}
                      size="small"
                      sx={{ ml: 1 }}
                      color="secondary"
                    />
                  )}
                </TableCell>
                <TableCell>
                  {consultation.confidence
                    ? `${(consultation.confidence * 100).toFixed(1)}%`
                    : 'N/A'}
                </TableCell>
                <TableCell>
                  {consultation.confirmed ? (
                    <Chip label="Confirmado" color="success" size="small" />
                  ) : (
                    <Chip label="Pendente" color="warning" size="small" />
                  )}
                </TableCell>
                <TableCell>
                  {!consultation.confirmed && (
                    <Button
                      size="small"
                      variant="outlined"
                      startIcon={<CheckCircleIcon />}
                      onClick={() => handleOpenConfirmDialog(consultation)}
                    >
                      Confirmar
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Dialog Nova Consulta */}
      <Dialog
        open={newConsultationOpen}
        onClose={() => setNewConsultationOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Nova Consulta</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nome do Paciente"
                value={formData.patientNome}
                onChange={(e) =>
                  setFormData({ ...formData, patientNome: e.target.value })
                }
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Sexo"
                value={formData.patientSexo}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    patientSexo: e.target.value as 'MASCULINO' | 'FEMININO' | 'OUTRO',
                  })
                }
                SelectProps={{
                  native: true,
                }}
              >
                <option value="MASCULINO">Masculino</option>
                <option value="FEMININO">Feminino</option>
                <option value="OUTRO">Outro</option>
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Data de Nascimento"
                type="date"
                value={formData.patientDataNascimento}
                onChange={(e) =>
                  setFormData({ ...formData, patientDataNascimento: e.target.value })
                }
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <Box
                {...getRootProps()}
                sx={{
                  border: '2px dashed',
                  borderColor: isDragActive ? 'primary.main' : 'grey.300',
                  borderRadius: 2,
                  p: 3,
                  textAlign: 'center',
                  cursor: 'pointer',
                  bgcolor: isDragActive ? 'action.hover' : 'background.paper',
                }}
              >
                <input {...getInputProps()} />
                <CloudUploadIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 1 }} />
                {formData.image ? (
                  <Typography>{formData.image.name}</Typography>
                ) : (
                  <Typography>
                    {isDragActive
                      ? 'Solte a imagem aqui'
                      : 'Arraste uma imagem ou clique para selecionar'}
                  </Typography>
                )}
              </Box>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setNewConsultationOpen(false)}>Cancelar</Button>
          <Button
            onClick={handleCreateConsultation}
            variant="contained"
            disabled={uploading || !formData.image || !formData.patientNome}
          >
            {uploading ? 'Enviando...' : 'Criar Consulta'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog Confirmar Diagnóstico */}
      <Dialog open={confirmDialogOpen} onClose={() => setConfirmDialogOpen(false)}>
        <DialogTitle>Confirmar Diagnóstico</DialogTitle>
        <DialogContent>
          {selectedConsultation && (
            <Box>
              <Alert severity="info" sx={{ mb: 2 }}>
                Diagnóstico da IA: {selectedConsultation.aiDiagnosis}
                {selectedConsultation.multClass && ` (${selectedConsultation.multClass})`}
                <br />
                Confiança: {selectedConsultation.confidence
                  ? `${(selectedConsultation.confidence * 100).toFixed(1)}%`
                  : 'N/A'}
              </Alert>
              <TextField
                fullWidth
                label="Diagnóstico Final"
                multiline
                rows={4}
                value={confirmDiagnosis}
                onChange={(e) => setConfirmDiagnosis(e.target.value)}
                placeholder="Você pode aceitar o diagnóstico da IA ou escrever outro"
              />
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDialogOpen(false)}>Cancelar</Button>
          <Button
            onClick={handleConfirmDiagnosis}
            variant="contained"
            disabled={!confirmDiagnosis}
          >
            Confirmar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default MedicoDashboard

