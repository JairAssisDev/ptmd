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
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Card,
  CardMedia,
  CardContent,
  IconButton,
  Divider,
} from '@mui/material'
import {
  Add as AddIcon,
  CloudUpload as CloudUploadIcon,
  CheckCircle as CheckCircleIcon,
  Info as InfoIcon,
  Close as CloseIcon,
} from '@mui/icons-material'
import { useDropzone } from 'react-dropzone'
import { format } from 'date-fns'
import {
  consultationService,
  ConsultationResponse,
  ConsultationRequest,
  ConfirmImageDiagnosisRequest,
} from '../services/consultationService'
import { Diagnosis, DiagnosisOptions, DiagnosisLabels } from '../types/diagnosis'

const MedicoDashboard = () => {
  const [consultations, setConsultations] = useState<ConsultationResponse[]>([])
  const [error, setError] = useState('')
  const [newConsultationOpen, setNewConsultationOpen] = useState(false)
  const [detailModalOpen, setDetailModalOpen] = useState(false)
  const [selectedConsultation, setSelectedConsultation] = useState<ConsultationResponse | null>(null)
  const [filterNome, setFilterNome] = useState('')
  const [filterCpf, setFilterCpf] = useState('')
  const [formData, setFormData] = useState({
    patientNome: '',
    patientCpf: '',
    patientSexo: 'MASCULINO' as 'MASCULINO' | 'FEMININO' | 'OUTRO',
    patientDataNascimento: '',
    images: [] as File[],
  })
  const [uploading, setUploading] = useState(false)

  useEffect(() => {
    loadConsultations()
  }, [])

  const loadConsultations = async (nome?: string, cpf?: string) => {
    try {
      const data = await consultationService.getMyConsultations(nome, cpf)
      setConsultations(data)
    } catch (err: any) {
      setError('Erro ao carregar consultas')
    }
  }

  const handleFilter = () => {
    loadConsultations(
      filterNome.trim() || undefined,
      filterCpf.trim() || undefined
    )
  }

  const handleClearFilters = () => {
    setFilterNome('')
    setFilterCpf('')
    loadConsultations()
  }

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    accept: {
      'image/*': ['.png', '.jpg', '.jpeg'],
    },
    onDrop: (acceptedFiles) => {
      setFormData({ ...formData, images: acceptedFiles })
    },
    multiple: true,
    maxFiles: 10,
  })

  const handleCreateConsultation = async () => {
    if (formData.images.length === 0) {
      setError('Por favor, selecione pelo menos uma imagem')
      return
    }

    if (!formData.patientNome || !formData.patientCpf) {
      setError('Nome e CPF do paciente são obrigatórios')
      return
    }

    setUploading(true)
    setError('')

    try {
      const request: ConsultationRequest = {
        patient: {
          nome: formData.patientNome,
          cpf: formData.patientCpf,
          sexo: formData.patientSexo,
          dataNascimento: formData.patientDataNascimento || undefined,
        },
        images: formData.images,
      }

      const newConsultation = await consultationService.createConsultation(request)
      setConsultations([newConsultation, ...consultations])
      setNewConsultationOpen(false)
      setFormData({
        patientNome: '',
        patientCpf: '',
        patientSexo: 'MASCULINO',
        patientDataNascimento: '',
        images: [],
      })
    } catch (err: any) {
      setError(err.response?.data?.error || err.response?.data || 'Erro ao criar consulta')
    } finally {
      setUploading(false)
    }
  }

  const handleOpenDetailModal = async (consultationId: number) => {
    try {
      setError('')
      const consultation = await consultationService.getConsultationById(consultationId)
      console.log('Consulta carregada:', consultation)
      console.log('Imagens da consulta:', consultation.images)
      setSelectedConsultation(consultation)
      setDetailModalOpen(true)
    } catch (err: any) {
      console.error('Erro ao carregar detalhes:', err)
      setError(err.response?.data?.error || err.message || 'Erro ao carregar detalhes da consulta')
    }
  }

  const handleConfirmImageDiagnosis = async (imageId: number, diagnosis: Diagnosis) => {
    if (!selectedConsultation) return

    try {
      const request: ConfirmImageDiagnosisRequest = {
        finalDiagnosis: diagnosis,
      }

      const updatedImage = await consultationService.confirmImageDiagnosis(imageId, request)

      // Atualizar a imagem na consulta selecionada
      const updatedConsultation = {
        ...selectedConsultation,
        images: selectedConsultation.images.map((img) =>
          img.id === imageId ? updatedImage : img
        ),
      }
      setSelectedConsultation(updatedConsultation)

      // Atualizar na lista também
      setConsultations(
        consultations.map((c) =>
          c.id === selectedConsultation.id ? updatedConsultation : c
        )
      )
    } catch (err: any) {
      setError(err.response?.data?.error || 'Erro ao confirmar diagnóstico')
    }
  }

  const getImageUrl = (filePath: string) => {
    if (!filePath) return ''
    // Extrair o nome do arquivo do caminho completo
    const filename = filePath.split(/[/\\]/).pop() || filePath
    const baseUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
    return `${baseUrl}/api/files/by-name/${encodeURIComponent(filename)}`
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

      {/* Filtros */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          Filtros
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={4}>
            <TextField
              fullWidth
              label="Nome do Paciente"
              value={filterNome}
              onChange={(e) => setFilterNome(e.target.value)}
              onKeyPress={(e) => {
                if (e.key === 'Enter') handleFilter()
              }}
            />
          </Grid>
          <Grid item xs={12} sm={4}>
            <TextField
              fullWidth
              label="CPF do Paciente"
              value={filterCpf}
              onChange={(e) => setFilterCpf(e.target.value)}
              onKeyPress={(e) => {
                if (e.key === 'Enter') handleFilter()
              }}
            />
          </Grid>
          <Grid item xs={12} sm={4}>
            <Box display="flex" gap={1}>
              <Button variant="contained" onClick={handleFilter}>
                Filtrar
              </Button>
              <Button variant="outlined" onClick={handleClearFilters}>
                Limpar
              </Button>
            </Box>
          </Grid>
        </Grid>
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Paciente</TableCell>
              <TableCell>CPF</TableCell>
              <TableCell>Data</TableCell>
              <TableCell>Imagens</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {consultations.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  <Typography color="text.secondary">
                    Nenhuma consulta encontrada
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              consultations.map((consultation) => (
                <TableRow key={consultation.id} hover>
                  <TableCell>{consultation.patient.nome}</TableCell>
                  <TableCell>{consultation.patient.cpf}</TableCell>
                  <TableCell>
                    {format(new Date(consultation.createdAt), 'dd/MM/yyyy HH:mm')}
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={`${consultation.images?.length || 0} imagem(ns)`}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    {consultation.images?.every((img) => img.confirmed) ? (
                      <Chip label="Todas confirmadas" color="success" size="small" />
                    ) : (
                      <Chip
                        label={`${consultation.images?.filter((img) => img.confirmed).length || 0}/${consultation.images?.length || 0} confirmadas`}
                        color="warning"
                        size="small"
                      />
                    )}
                  </TableCell>
                  <TableCell>
                    <Button
                      size="small"
                      variant="outlined"
                      startIcon={<InfoIcon />}
                      onClick={() => handleOpenDetailModal(consultation.id)}
                    >
                      Mais Informações
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
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
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="CPF do Paciente"
                value={formData.patientCpf}
                onChange={(e) =>
                  setFormData({ ...formData, patientCpf: e.target.value })
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
                {formData.images.length > 0 ? (
                  <Box>
                    <Typography variant="body1" gutterBottom>
                      {formData.images.length} imagem(ns) selecionada(s):
                    </Typography>
                    {formData.images.map((img, idx) => (
                      <Chip
                        key={idx}
                        label={img.name}
                        onDelete={() => {
                          const newImages = formData.images.filter((_, i) => i !== idx)
                          setFormData({ ...formData, images: newImages })
                        }}
                        sx={{ m: 0.5 }}
                      />
                    ))}
                  </Box>
                ) : (
                  <Typography>
                    {isDragActive
                      ? 'Solte as imagens aqui'
                      : 'Arraste imagens ou clique para selecionar (máximo 10)'}
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
            disabled={uploading || formData.images.length === 0 || !formData.patientNome || !formData.patientCpf}
          >
            {uploading ? 'Enviando...' : 'Criar Consulta'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Modal de Detalhes da Consulta */}
      <Dialog
        open={detailModalOpen}
        onClose={() => setDetailModalOpen(false)}
        maxWidth="lg"
        fullWidth
      >
        <DialogTitle>
          <Box display="flex" justifyContent="space-between" alignItems="center">
            <Typography variant="h6">
              Detalhes da Consulta
              {selectedConsultation && ` - ${selectedConsultation.patient.nome}`}
            </Typography>
            <IconButton onClick={() => setDetailModalOpen(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent>
          {selectedConsultation && (
            <Box>
              <Grid container spacing={2} sx={{ mb: 3 }}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Paciente
                  </Typography>
                  <Typography variant="body1">{selectedConsultation.patient.nome}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    CPF
                  </Typography>
                  <Typography variant="body1">{selectedConsultation.patient.cpf}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Data da Consulta
                  </Typography>
                  <Typography variant="body1">
                    {format(new Date(selectedConsultation.createdAt), 'dd/MM/yyyy HH:mm')}
                  </Typography>
                </Grid>
              </Grid>

              <Divider sx={{ my: 3 }} />

              <Typography variant="h6" gutterBottom>
                Imagens ({selectedConsultation.images?.length || 0})
              </Typography>

              <Grid container spacing={2}>
                {selectedConsultation.images && selectedConsultation.images.length > 0 ? (
                  selectedConsultation.images.map((image) => {
                    // Função para mapear diagnóstico da IA para o enum
                    const mapAiDiagnosisToEnum = (aiDiagnosis?: string): Diagnosis => {
                      if (!aiDiagnosis) return Diagnosis.NORMAL
                      const normalized = aiDiagnosis.toLowerCase().trim()
                      if (normalized === 'normal') return Diagnosis.NORMAL
                      if (normalized === 'aom') return Diagnosis.AOM
                      if (normalized === 'csom') return Diagnosis.CSOM
                      if (normalized === 'earwax') return Diagnosis.EARWAX
                      if (normalized === 'externalearinfections' || normalized.includes('external')) return Diagnosis.EXTERNAL_EAR_INFECTIONS
                      if (normalized === 'tympanoskleros') return Diagnosis.TYMPANOSKLEROS
                      return Diagnosis.NORMAL
                    }

                    const aiDiagnosisEnum = image.aiDiagnosis ? mapAiDiagnosisToEnum(image.aiDiagnosis) : null
                    const currentDiagnosis = image.finalDiagnosis 
                      ? (DiagnosisOptions.find(opt => opt.value === image.finalDiagnosis)?.value as Diagnosis || mapAiDiagnosisToEnum(image.finalDiagnosis))
                      : (aiDiagnosisEnum || Diagnosis.NORMAL)

                    return (
                      <Grid item xs={12} md={6} key={image.id}>
                        <Card>
                          <CardMedia
                            component="img"
                            height="200"
                            image={getImageUrl(image.filePath)}
                            alt={image.fileName}
                            sx={{ objectFit: 'contain', bgcolor: 'grey.100' }}
                            onError={(e: any) => {
                              e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2RkZCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTQiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5JbWFnZW0gbsOjbyBjYXJyZWdhZGE8L3RleHQ+PC9zdmc+'
                            }}
                          />
                          <CardContent>
                            <Typography variant="subtitle2" gutterBottom>
                              {image.fileName}
                            </Typography>
                            {(image.aiDiagnosis || image.multClass) && (
                              <Alert severity="info" sx={{ mb: 1 }}>
                                <Typography variant="caption" display="block" fontWeight="bold">
                                  Diagnóstico IA: {image.aiDiagnosis || 'N/A'}
                                </Typography>
                                {image.multClass && (
                                  <Typography variant="caption" display="block">
                                    MultClass: {image.multClass}
                                  </Typography>
                                )}
                                {image.confidence !== null && image.confidence !== undefined && (
                                  <Typography variant="caption" display="block">
                                    Confiança: {(image.confidence * 100).toFixed(1)}%
                                  </Typography>
                                )}
                              </Alert>
                            )}
                            {image.confirmed ? (
                              <Alert severity="success" sx={{ mb: 1 }}>
                                <Typography variant="caption" display="block" fontWeight="bold">
                                  Diagnóstico Confirmado: {image.finalDiagnosis && DiagnosisLabels[image.finalDiagnosis as Diagnosis] || image.finalDiagnosis}
                                </Typography>
                              </Alert>
                            ) : (
                              <Box>
                                <FormControl fullWidth size="small" sx={{ mb: 1 }}>
                                  <InputLabel>Confirmar Diagnóstico</InputLabel>
                                  <Select
                                    value={currentDiagnosis}
                                    label="Confirmar Diagnóstico"
                                    onChange={(e) => {
                                      const diagnosis = e.target.value as Diagnosis
                                      handleConfirmImageDiagnosis(image.id, diagnosis)
                                    }}
                                  >
                                    {DiagnosisOptions.map((option) => (
                                      <MenuItem key={option.value} value={option.value}>
                                        {option.label}
                                      </MenuItem>
                                    ))}
                                  </Select>
                                </FormControl>
                                <Button
                                  size="small"
                                  variant="contained"
                                  startIcon={<CheckCircleIcon />}
                                  fullWidth
                                  onClick={() => {
                                    handleConfirmImageDiagnosis(image.id, currentDiagnosis)
                                  }}
                                >
                                  Confirmar
                                </Button>
                              </Box>
                            )}
                          </CardContent>
                        </Card>
                      </Grid>
                    )
                  })
                ) : (
                  <Grid item xs={12}>
                    <Alert severity="info">
                      Nenhuma imagem encontrada para esta consulta.
                    </Alert>
                  </Grid>
                )}
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDetailModalOpen(false)}>Fechar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default MedicoDashboard
